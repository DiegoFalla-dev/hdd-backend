package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
