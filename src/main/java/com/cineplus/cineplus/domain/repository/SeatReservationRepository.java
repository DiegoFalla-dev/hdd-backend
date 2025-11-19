package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.ReservationStatus;
import com.cineplus.cineplus.domain.entity.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {
    Optional<SeatReservation> findByToken(String token);

    List<SeatReservation> findAllByToken(String token);

    @Query("select r from SeatReservation r where r.status = :status and r.expiresAt <= :time")
    List<SeatReservation> findByStatusAndExpired(@Param("status") ReservationStatus status, @Param("time") LocalDateTime time);
}
