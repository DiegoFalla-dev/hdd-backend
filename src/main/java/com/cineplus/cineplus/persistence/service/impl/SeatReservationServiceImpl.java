package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.entity.Seat;
import com.cineplus.cineplus.domain.entity.Seat.SeatStatus;
import com.cineplus.cineplus.domain.entity.SeatReservation;
import com.cineplus.cineplus.domain.entity.Showtime;
import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.repository.SeatRepository;
import com.cineplus.cineplus.domain.repository.SeatReservationRepository;
import com.cineplus.cineplus.domain.repository.ShowtimeRepository;
import com.cineplus.cineplus.domain.repository.UserRepository;
import com.cineplus.cineplus.domain.service.SeatReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatReservationServiceImpl implements SeatReservationService {

    private final SeatRepository seatRepository;
    private final SeatReservationRepository reservationRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;

    private static final int RESERVATION_DURATION_MINUTES = 1;

    @Override
    @Transactional
    public String initiateSeatReservation(Long showtimeId, Set<String> seatIdentifiers, Long userId) {
        // Validar que el showtime exista
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found"));

        // Validar que todos los asientos estén disponibles
        List<Seat> seats = seatRepository.findByShowtimeIdAndSeatIdentifierIn(showtimeId, seatIdentifiers);
        
        if (seats.size() != seatIdentifiers.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some seats do not exist");
        }

        // Verificar que todos estén AVAILABLE y no cancelados
        List<Seat> unavailableSeats = seats.stream()
                .filter(seat -> !seat.getStatus().equals(SeatStatus.AVAILABLE) || seat.getIsCancelled())
                .toList();

        if (!unavailableSeats.isEmpty()) {
            String unavailableIds = unavailableSeats.stream()
                    .map(Seat::getSeatIdentifier)
                    .collect(Collectors.joining(", "));
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "The following seats are not available: " + unavailableIds);
        }

        // Generar sessionId único
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusMinutes(RESERVATION_DURATION_MINUTES);

        // Crear la reserva
        SeatReservation reservation = new SeatReservation();
        reservation.setSessionId(sessionId);
        reservation.setShowtime(showtime);
        reservation.setCreatedAt(now);
        reservation.setExpiryTime(expiryTime);
        reservation.setIsActive(true);
        reservation.setIsConfirmed(false);
        reservation.setSeatIdentifiers(new HashSet<>(seatIdentifiers));

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            reservation.setUser(user);
        }

        reservationRepository.save(reservation);

        // Actualizar los asientos a TEMPORARILY_RESERVED
        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.TEMPORARILY_RESERVED);
            seat.setSessionId(sessionId);
            seat.setReservationTime(now);
        }
        seatRepository.saveAll(seats);

        // Actualizar contador de asientos disponibles
        showtime.setAvailableSeats(showtime.getAvailableSeats() - seats.size());
        showtimeRepository.save(showtime);

        log.info("Initiated seat reservation. SessionId: {}, Showtime: {}, Seats: {}", 
                sessionId, showtimeId, seatIdentifiers);

        return sessionId;
    }

    @Override
    @Transactional
    public void releaseReservationBySession(String sessionId) {
        SeatReservation reservation = reservationRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Reservation not found with sessionId: " + sessionId));

        if (!reservation.getIsActive()) {
            log.warn("Attempting to release inactive reservation: {}", sessionId);
            return;
        }

        // Obtener los asientos asociados
        List<Seat> seats = seatRepository.findBySessionId(sessionId);

        // Solo liberar asientos que NO están permanentemente cancelados
        List<Seat> seatsToRelease = seats.stream()
                .filter(seat -> !seat.getIsCancelled())
                .toList();

        // Actualizar asientos a AVAILABLE
        for (Seat seat : seatsToRelease) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setSessionId(null);
            seat.setReservationTime(null);
        }
        seatRepository.saveAll(seatsToRelease);

        // Actualizar contador de asientos disponibles
        Showtime showtime = reservation.getShowtime();
        showtime.setAvailableSeats(showtime.getAvailableSeats() + seatsToRelease.size());
        showtimeRepository.save(showtime);

        // Marcar la reserva como inactiva
        reservation.cancel();
        reservationRepository.save(reservation);

        log.info("Released reservation. SessionId: {}, Released {} seats", sessionId, seatsToRelease.size());
    }

    @Override
    @Transactional
    public void confirmReservation(String sessionId, String purchaseNumber) {
        SeatReservation reservation = reservationRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Reservation not found with sessionId: " + sessionId));

        if (!reservation.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Reservation is not active");
        }

        if (reservation.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Reservation has expired");
        }

        // Confirmar los asientos como OCCUPIED
        int updatedCount = seatRepository.confirmSeatsWithPurchaseNumber(
                sessionId, SeatStatus.OCCUPIED, SeatStatus.TEMPORARILY_RESERVED, purchaseNumber);

        if (updatedCount == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Could not confirm seats. They may have been released.");
        }

        // Confirmar la reserva
        reservation.confirm(purchaseNumber);
        reservationRepository.save(reservation);

        log.info("Confirmed reservation. SessionId: {}, PurchaseNumber: {}, Seats: {}", 
                sessionId, purchaseNumber, updatedCount);
    }

    @Override
    @Transactional
    public void cancelSeatsPermanently(Long showtimeId, Set<String> seatIdentifiers, String purchaseNumber) {
        if (purchaseNumber == null || purchaseNumber.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Purchase number is required for permanent cancellation");
        }

        // Validar que los asientos existan
        List<Seat> seats = seatRepository.findByShowtimeIdAndSeatIdentifierIn(showtimeId, seatIdentifiers);
        
        if (seats.size() != seatIdentifiers.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some seats do not exist");
        }

        // Cancelar permanentemente
        int cancelledCount = seatRepository.cancelSeatsPermanently(showtimeId, seatIdentifiers);

        log.info("Permanently cancelled {} seats for showtime: {}, purchaseNumber: {}", 
                cancelledCount, showtimeId, purchaseNumber);
    }

    @Override
    @Transactional
    public void releaseOccupiedSeats(Long showtimeId, Set<String> seatIdentifiers) {
        List<Seat> seats = seatRepository.findByShowtimeIdAndSeatIdentifierIn(showtimeId, seatIdentifiers);

        // Solo liberar los que están OCCUPIED y NO están permanentemente cancelados
        List<Seat> seatsToRelease = seats.stream()
                .filter(seat -> seat.getStatus().equals(SeatStatus.OCCUPIED) && !seat.getIsCancelled())
                .toList();

        if (seatsToRelease.isEmpty()) {
            log.warn("No occupied seats to release for showtime: {}", showtimeId);
            return;
        }

        // Actualizar a AVAILABLE
        for (Seat seat : seatsToRelease) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setPurchaseNumber(null);
        }
        seatRepository.saveAll(seatsToRelease);

        // Actualizar contador
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found"));
        showtime.setAvailableSeats(showtime.getAvailableSeats() + seatsToRelease.size());
        showtimeRepository.save(showtime);

        log.info("Released {} occupied seats for showtime: {}", seatsToRelease.size(), showtimeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getReservedSeatsBySession(String sessionId) {
        List<Seat> seats = seatRepository.findBySessionId(sessionId);
        return seats.stream()
                .map(Seat::getSeatIdentifier)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void releaseExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        
        // Buscar reservas expiradas
        List<SeatReservation> expiredReservations = reservationRepository.findExpiredActiveReservations(now);

        if (expiredReservations.isEmpty()) {
            return;
        }

        log.info("Found {} expired reservations to release", expiredReservations.size());

        for (SeatReservation reservation : expiredReservations) {
            try {
                releaseReservationBySession(reservation.getSessionId());
            } catch (Exception e) {
                log.error("Error releasing expired reservation: {}", reservation.getSessionId(), e);
            }
        }
    }
}
