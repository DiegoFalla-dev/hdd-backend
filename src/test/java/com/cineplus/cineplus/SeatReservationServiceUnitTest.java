package com.cineplus.cineplus;

import com.cineplus.cineplus.domain.entity.Seat;
 
import com.cineplus.cineplus.domain.repository.SeatRepository;
import com.cineplus.cineplus.domain.repository.SeatReservationRepository;
import com.cineplus.cineplus.persistence.mapper.SeatMapper;
import com.cineplus.cineplus.persistence.service.impl.SeatReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class SeatReservationServiceUnitTest {

    private SeatRepository seatRepository;
    private SeatReservationRepository reservationRepository;
    private SeatMapper seatMapper;

    @BeforeEach
    public void setup() {
        seatRepository = Mockito.mock(SeatRepository.class);
        reservationRepository = Mockito.mock(SeatReservationRepository.class);
        seatMapper = Mockito.mock(SeatMapper.class);
    }

    @Test
    public void reserve_conflict_when_seat_already_held() throws Exception {
        Seat s = new Seat();
        s.setId(1L);
        s.setSeatIdentifier("A1");
        s.setStatus(Seat.SeatStatus.TEMPORARILY_RESERVED);

        when(seatRepository.lockSeatsForUpdate(anyList())).thenReturn(List.of(s));

        SeatReservationServiceImpl svc = new SeatReservationServiceImpl(seatRepository, reservationRepository, seatMapper, null, 600, 10);

        assertThrows(IllegalStateException.class, () -> svc.reserveSeats(1L, List.of(1L), null));
    }
}
