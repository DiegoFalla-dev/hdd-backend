package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Purchase;
import com.cineplus.cineplus.domain.entity.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /**
     * Buscar compra por número de orden
     */
    Optional<Purchase> findByPurchaseNumber(String purchaseNumber);

    /**
     * Buscar compra por sessionId de reserva
     */
    Optional<Purchase> findBySessionId(String sessionId);

    /**
     * Obtener todas las compras de un usuario, ordenadas por fecha descendente
     */
    @Query("SELECT p FROM Purchase p WHERE p.user.id = :userId ORDER BY p.purchaseDate DESC")
    List<Purchase> findByUserIdOrderByPurchaseDateDesc(@Param("userId") Long userId);

    /**
     * Obtener compras de un usuario en un rango de fechas
     */
    @Query("SELECT p FROM Purchase p WHERE p.user.id = :userId " +
           "AND p.purchaseDate BETWEEN :startDate AND :endDate " +
           "ORDER BY p.purchaseDate DESC")
    List<Purchase> findByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Obtener compras por estado
     */
    List<Purchase> findByStatus(PurchaseStatus status);

    /**
     * Verificar si existe una compra con un purchaseNumber específico
     */
    boolean existsByPurchaseNumber(String purchaseNumber);

    /**
     * Contar compras de un usuario por estado
     */
    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.user.id = :userId AND p.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") PurchaseStatus status);
}
