package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Seat;
import com.cineplus.cineplus.domain.entity.Seat.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowtimeId(Long showtimeId);
    List<Seat> findByShowtimeIdAndSeatIdentifierIn(Long showtimeId, Set<String> seatIdentifiers);
    
    // Buscar asientos por sessionId
    List<Seat> findBySessionId(String sessionId);
    
    // Buscar asientos por sessionId y showtime
    List<Seat> findBySessionIdAndShowtimeId(String sessionId, Long showtimeId);
    
    // Buscar asientos temporalmente reservados expirados
    @Query("SELECT s FROM Seat s WHERE s.status = :status AND s.reservationTime < :expiryTime AND s.isCancelled = false")
    List<Seat> findExpiredTemporaryReservations(
        @Param("status") SeatStatus status,
        @Param("expiryTime") LocalDateTime expiryTime
    );
    
    // Buscar asientos por coordenadas (para manejo de bloques)
    @Query("SELECT s FROM Seat s WHERE s.showtime.id = :showtimeId " +
           "AND s.rowPosition BETWEEN :minRow AND :maxRow " +
           "AND s.colPosition BETWEEN :minCol AND :maxCol")
    List<Seat> findByShowtimeIdAndCoordinateRange(
        @Param("showtimeId") Long showtimeId,
        @Param("minRow") Integer minRow,
        @Param("maxRow") Integer maxRow,
        @Param("minCol") Integer minCol,
        @Param("maxCol") Integer maxCol
    );

    @Modifying
    @Query("UPDATE Seat s SET s.status = :newStatus WHERE s.showtime.id = :showtimeId AND s.seatIdentifier IN :seatIdentifiers AND s.status = :expectedStatus")
    int updateSeatStatusIfExpected(
            @Param("showtimeId") Long showtimeId,
            @Param("seatIdentifiers") Set<String> seatIdentifiers,
            @Param("newStatus") SeatStatus newStatus,
            @Param("expectedStatus") SeatStatus expectedStatus
    );
    
    // Actualizar estado de asientos por sessionId
    @Modifying
    @Query("UPDATE Seat s SET s.status = :newStatus, s.sessionId = null, s.reservationTime = null " +
           "WHERE s.sessionId = :sessionId AND s.status = :expectedStatus")
    int updateSeatStatusBySessionId(
        @Param("sessionId") String sessionId,
        @Param("newStatus") SeatStatus newStatus,
        @Param("expectedStatus") SeatStatus expectedStatus
    );
    
    // Confirmar asientos con número de compra
    @Modifying
    @Query("UPDATE Seat s SET s.status = :newStatus, s.purchaseNumber = :purchaseNumber " +
           "WHERE s.sessionId = :sessionId AND s.status = :expectedStatus")
    int confirmSeatsWithPurchaseNumber(
        @Param("sessionId") String sessionId,
        @Param("newStatus") SeatStatus newStatus,
        @Param("expectedStatus") SeatStatus expectedStatus,
        @Param("purchaseNumber") String purchaseNumber
    );
    
    // Cancelar asientos permanentemente
    @Modifying
    @Query("UPDATE Seat s SET s.status = 'CANCELLED', s.isCancelled = true " +
           "WHERE s.showtime.id = :showtimeId AND s.seatIdentifier IN :seatIdentifiers")
    int cancelSeatsPermanently(
        @Param("showtimeId") Long showtimeId,
        @Param("seatIdentifiers") Set<String> seatIdentifiers
    );
    
    // Buscar asiento por showtime y coordenadas específicas
    Optional<Seat> findByShowtimeIdAndRowPositionAndColPosition(
        Long showtimeId, Integer rowPosition, Integer colPosition
    );
}
