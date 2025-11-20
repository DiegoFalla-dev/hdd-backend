package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowtimeId(Long showtimeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id in :ids")
    List<Seat> lockSeatsForUpdate(@Param("ids") List<Long> ids);

    List<Seat> findByShowtimeIdAndSeatIdentifierIn(Long showtimeId, java.util.Set<String> seatIdentifiers);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Seat s SET s.status = :newStatus WHERE s.showtime.id = :showtimeId AND s.seatIdentifier IN :seatIdentifiers AND s.status = :expectedStatus")
    int updateSeatStatusIfExpected(@Param("showtimeId") Long showtimeId,
                                    @Param("seatIdentifiers") java.util.Set<String> seatIdentifiers,
                                    @Param("newStatus") Seat.SeatStatus newStatus,
                                    @Param("expectedStatus") Seat.SeatStatus expectedStatus);
}
