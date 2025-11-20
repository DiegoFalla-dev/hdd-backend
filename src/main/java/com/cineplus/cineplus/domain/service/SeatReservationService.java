package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.ReservationResponseDto;
import com.cineplus.cineplus.domain.dto.SeatDto;

import java.util.List;

public interface SeatReservationService {
    List<SeatDto> getSeatsForShowtime(Long showtimeId, boolean includeHoldInfo);

    ReservationResponseDto reserveSeats(Long showtimeId, List<Long> seatIds, Long userId) throws Exception;

    Long confirmReservation(Long showtimeId, Long reservationId, String token) throws Exception;

    void cancelReservation(Long showtimeId, Long reservationId) throws Exception;

    void releaseExpiredReservations();
}
