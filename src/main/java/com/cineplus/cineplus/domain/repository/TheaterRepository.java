package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {
    List<Theater> findByCinemaId(Long cinemaId);
}