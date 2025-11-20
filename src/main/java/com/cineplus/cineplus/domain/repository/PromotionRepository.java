package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // Buscar una promoción por su código
    Optional<Promotion> findByCode(String code);

    // Buscar promociones activas en un rango de fechas
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(LocalDateTime now1, LocalDateTime now2);

    // Buscar promociones activas por código y que sean válidas en una fecha dada
    Optional<Promotion> findByCodeAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(String code, LocalDateTime now1, LocalDateTime now2);

    // Buscar promociones por estado activo
    List<Promotion> findByIsActive(Boolean isActive);
}