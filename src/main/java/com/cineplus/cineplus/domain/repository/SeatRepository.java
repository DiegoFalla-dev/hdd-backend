package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Seat;
import com.cineplus.cineplus.domain.entity.Seat.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowtimeId(Long showtimeId);
    List<Seat> findByShowtimeIdAndSeatIdentifierIn(Long showtimeId, Set<String> seatIdentifiers);

    void deleteByShowtimeId(Long showtimeId);

    @Modifying
    @Query("UPDATE Seat s SET s.status = :newStatus WHERE s.showtime.id = :showtimeId AND s.seatIdentifier IN :seatIdentifiers AND s.status = :expectedStatus")
    int updateSeatStatusIfExpected(
            @Param("showtimeId") Long showtimeId,
            @Param("seatIdentifiers") Set<String> seatIdentifiers,
            @Param("newStatus") SeatStatus newStatus,
            @Param("expectedStatus") SeatStatus expectedStatus
    );
}