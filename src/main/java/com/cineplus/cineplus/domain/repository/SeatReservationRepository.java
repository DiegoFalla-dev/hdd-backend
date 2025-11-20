package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {
    
    // Buscar reserva por sessionId
    Optional<SeatReservation> findBySessionId(String sessionId);
    
    // Buscar reservas activas por usuario
    List<SeatReservation> findByUserIdAndIsActiveTrue(Long userId);
    
    // Buscar reservas por showtime
    List<SeatReservation> findByShowtimeIdAndIsActiveTrue(Long showtimeId);
    
    // Buscar reservas expiradas y activas (para liberarlas)
    @Query("SELECT sr FROM SeatReservation sr WHERE sr.isActive = true AND sr.isConfirmed = false AND sr.expiryTime < :currentTime")
    List<SeatReservation> findExpiredActiveReservations(@Param("currentTime") LocalDateTime currentTime);
    
    // Buscar reservas por número de compra
    Optional<SeatReservation> findByPurchaseNumber(String purchaseNumber);
}
