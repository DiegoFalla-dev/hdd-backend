package com.cineplus.cineplus.service;

import com.cineplus.cineplus.persistance.entity.*;
import com.cineplus.cineplus.persistance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final RedisLockService redisLockService;
    private static final String LOCK_PREFIX = "seat-lock:";

    @Transactional
    public Booking confirmBooking(String holderId, String userExternalId, Long showId, List<Long> seatIds) {
        // check locks exist and belong to holder
        for (Long seatId : seatIds) {
            String key = LOCK_PREFIX + showId + ":" + seatId;
            String val = redisLockService.redisTemplate().opsForValue().get(key); // note: expose or inject template if necessary
            if (!holderId.equals(val)) throw new IllegalStateException("No lock for seat " + seatId);
        }

        // fetch seats (optimistic locking by @Version)
        List<Seat> seats = seatRepository.findAllById(seatIds);
        // double-check availability and set to BOOKED
        int total = 0;
        for (Seat s : seats) {
            if (s.getStatus() == Seat.SeatStatus.BOOKED) throw new IllegalStateException("Seat already booked " + s.getId());
            s.setStatus(Seat.SeatStatus.BOOKED);
            total += 0; // price logic: use show.basePrice or seat-specific
        }
        seatRepository.saveAll(seats);

        Booking booking = Booking.builder()
                .userExternalId(userExternalId)
                .createdAt(OffsetDateTime.now())
                .totalCents(0)
                .status(Booking.BookingStatus.PAID) // or PENDING if payment required
                .build();
        Booking saved = bookingRepository.save(booking);
        // create booking seats
        List<BookingSeat> bSeats = seats.stream().map(s -> BookingSeat.builder()
                .booking(saved)
                .seat(s)
                .priceCents(0)
                .build()).collect(Collectors.toList());
        saved.setBookingSeats(bSeats);
        saved = bookingRepository.save(saved);

        // release locks
        for (Long seatId : seatIds) redisLockService.release(LOCK_PREFIX + showId + ":" + seatId, holderId);

        return saved;
    }
}