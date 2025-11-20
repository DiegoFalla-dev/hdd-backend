package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.ReservationResponseDto;
import com.cineplus.cineplus.domain.dto.SeatDto;
import com.cineplus.cineplus.domain.entity.ReservationStatus;
import com.cineplus.cineplus.domain.entity.Seat;
import com.cineplus.cineplus.domain.entity.SeatReservation;
// SeatState enum not used; using Seat.SeatStatus on entity
import com.cineplus.cineplus.domain.repository.SeatRepository;
import com.cineplus.cineplus.domain.repository.SeatReservationRepository;
import com.cineplus.cineplus.domain.repository.OrderRepository;
import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.OrderItem;
import com.cineplus.cineplus.domain.service.SeatReservationService;
import com.cineplus.cineplus.persistence.mapper.SeatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatReservationServiceImpl implements SeatReservationService {

    private final SeatRepository seatRepository;
    private final SeatReservationRepository reservationRepository;
    private final SeatMapper seatMapper;
    private final SimpMessagingTemplate messagingTemplate;

    private final long ttlSeconds;
    private final int maxSeats;

    private static final Logger log = LoggerFactory.getLogger(SeatReservationServiceImpl.class);

    private final OrderRepository orderRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public SeatReservationServiceImpl(SeatRepository seatRepository,
                                      SeatReservationRepository reservationRepository,
                                      OrderRepository orderRepository,
                                      SeatMapper seatMapper,
                                      SimpMessagingTemplate messagingTemplate,
                                      @Value("${seats.ttl.seconds:600}") long ttlSeconds,
                                      @Value("${seats.max-per-reservation:10}") int maxSeats) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
        this.orderRepository = orderRepository;
        this.seatMapper = seatMapper;
        this.messagingTemplate = messagingTemplate;
        this.ttlSeconds = ttlSeconds;
        this.maxSeats = maxSeats;
    }

    /** Compatibility constructor for existing unit tests that don't provide OrderRepository. */
    public SeatReservationServiceImpl(SeatRepository seatRepository,
                                      SeatReservationRepository reservationRepository,
                                      SeatMapper seatMapper,
                                      SimpMessagingTemplate messagingTemplate,
                                      @Value("${seats.ttl.seconds:600}") long ttlSeconds,
                                      @Value("${seats.max-per-reservation:10}") int maxSeats) {
        this(seatRepository, reservationRepository, null, seatMapper, messagingTemplate, ttlSeconds, maxSeats);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatDto> getSeatsForShowtime(Long showtimeId, boolean includeHoldInfo) {
        List<Seat> seats = seatRepository.findByShowtimeId(showtimeId);
        List<SeatDto> dtos = seatMapper.toDtoList(seats);
        return dtos;
    }

    @Override
    @Transactional
    public ReservationResponseDto reserveSeats(Long showtimeId, List<Long> seatIds, Long userId) throws Exception {
        if (seatIds == null || seatIds.isEmpty()) throw new IllegalArgumentException("seatIds required");
        if (seatIds.size() > maxSeats) throw new IllegalArgumentException("Too many seats requested");

        // Lock seats pessimistically
        List<Seat> seats = seatRepository.lockSeatsForUpdate(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new IllegalStateException("Some seats not found");
        }

        // Validate availability (use entity SeatStatus)
        for (Seat s : seats) {
            if (s.getStatus() != Seat.SeatStatus.AVAILABLE) {
                throw new IllegalStateException("Seat " + s.getSeatIdentifier() + " not available");
            }
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(ttlSeconds);

        // Create reservations
        List<SeatReservation> reservations = new ArrayList<>();
        for (Seat s : seats) {
            s.setStatus(Seat.SeatStatus.TEMPORARILY_RESERVED);
            SeatReservation r = new SeatReservation();
            r.setShowtimeId(showtimeId);
            r.setSeat(s);
            r.setUserId(userId);
            r.setStatus(ReservationStatus.HELD);
            r.setToken(token);
            r.setCreatedAt(now);
            r.setExpiresAt(expiresAt);
            reservations.add(r);
        }

        seatRepository.saveAll(seats);
        reservationRepository.saveAll(reservations);

        // Publish websocket update
        publishSeatUpdate(showtimeId, seats);

        List<Long> reservedSeatIds = seats.stream().map(Seat::getId).collect(Collectors.toList());
        Long reservationId = reservations.isEmpty() ? null : reservations.get(0).getId();
        return new ReservationResponseDto(reservationId, token, expiresAt, reservedSeatIds);
    }

    @Override
    @Transactional
    public Long confirmReservation(Long showtimeId, Long reservationId, String token) throws Exception {
        // Find all reservations by token
        List<SeatReservation> reservations = reservationRepository.findAllByToken(token);
        if (reservations == null || reservations.isEmpty()) {
            throw new IllegalStateException("Reservation not found");
        }

        // Validate and handle idempotency
        LocalDateTime now = LocalDateTime.now();
        for (SeatReservation r : reservations) {
            if (!Objects.equals(r.getShowtimeId(), showtimeId)) throw new IllegalStateException("Showtime mismatch");
            if (r.getExpiresAt().isBefore(now)) throw new IllegalStateException("Reservation expired");
            if (r.getStatus() == ReservationStatus.CONFIRMED) {
                // Already confirmed -> idempotent: return associated orderId if present
                if (r.getOrderId() != null) return r.getOrderId();
                throw new IllegalStateException("Reservation already confirmed but no order id present");
            }
            if (r.getStatus() != ReservationStatus.HELD) throw new IllegalStateException("Reservation not held");
        }

        // Lock seats
        List<Long> seatIds = reservations.stream().map(r -> r.getSeat().getId()).collect(Collectors.toList());
        List<Seat> seats = seatRepository.lockSeatsForUpdate(seatIds);

        // Ensure seats are held
        for (Seat s : seats) {
            if (s.getStatus() != Seat.SeatStatus.TEMPORARILY_RESERVED) throw new IllegalStateException("Seat state invalid for confirmation");
        }

        // Create and persist Order with OrderItems atomically within this transaction
        Order order = new Order();
        order.setShowtimeId(showtimeId);
        // set userId from reservations (assume same user for all)
        order.setUserId(reservations.get(0).getUserId());
        order.setCreatedAt(LocalDateTime.now());

        for (Seat s : seats) {
            OrderItem item = new OrderItem();
            item.setSeatId(s.getId());
            item.setSeatIdentifier(s.getSeatIdentifier());
            order.addItem(item);
        }

        Order persisted = orderRepository.save(order);

        // Mark seats occupied and attach orderId to reservations
        for (Seat s : seats) s.setStatus(Seat.SeatStatus.OCCUPIED);
        seatRepository.saveAll(seats);

        for (SeatReservation r : reservations) {
            r.setStatus(ReservationStatus.CONFIRMED);
            r.setOrderId(persisted.getId());
        }
        reservationRepository.saveAll(reservations);

        publishSeatUpdate(showtimeId, seats);

        return persisted.getId();
    }

    @Override
    @Transactional
    public void cancelReservation(Long showtimeId, Long reservationId) throws Exception {
        Optional<SeatReservation> opt = reservationRepository.findById(reservationId);
        if (opt.isEmpty()) return;
        SeatReservation r = opt.get();
        if (r.getStatus() != ReservationStatus.HELD) return;

        Seat seat = r.getSeat();
        seat.setStatus(Seat.SeatStatus.AVAILABLE);
        r.setStatus(ReservationStatus.RELEASED);
        reservationRepository.save(r);
        seatRepository.save(seat);

        publishSeatUpdate(showtimeId, Collections.singletonList(seat));
    }

    @Override
    @Scheduled(fixedDelayString = "${seats.expiration.poll-ms:60000}")
    @Transactional
    public void releaseExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<SeatReservation> expired = reservationRepository.findByStatusAndExpired(ReservationStatus.HELD, now);
        if (expired == null || expired.isEmpty()) return;
        log.debug("Found {} expired reservations", expired.size());

        Map<Long, Seat> changedSeats = new HashMap<>();
        for (SeatReservation r : expired) {
            r.setStatus(ReservationStatus.RELEASED);
            Seat seat = r.getSeat();
            seat.setStatus(Seat.SeatStatus.AVAILABLE);
            changedSeats.put(seat.getId(), seat);
        }

        reservationRepository.saveAll(expired);
        seatRepository.saveAll(changedSeats.values());

        if (!changedSeats.isEmpty()) {
            publishSeatUpdate(expired.get(0).getShowtimeId(), new ArrayList<>(changedSeats.values()));
        }
    }

    private void publishSeatUpdate(Long showtimeId, List<Seat> seats) {
        try {
            List<Map<String, Object>> seatsPayload = seats.stream().map(s -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", s.getId());
                    m.put("state", s.getStatus().name());
                return m;
            }).collect(Collectors.toList());

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "SEAT_UPDATE");
            payload.put("showtimeId", showtimeId);
            payload.put("seats", seatsPayload);

            messagingTemplate.convertAndSend("/topic/seats." + showtimeId, payload);
        } catch (Exception ex) {
            log.error("Failed publish seat update", ex);
        }
    }
}
