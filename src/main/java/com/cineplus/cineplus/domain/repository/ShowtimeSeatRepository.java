package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.ShowtimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, Long> {
    // Métodos personalizados si se requieren
}
