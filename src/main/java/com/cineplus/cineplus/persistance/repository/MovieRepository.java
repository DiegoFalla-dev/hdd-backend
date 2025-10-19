package com.cineplus.cineplus.persistance.repository;

import com.cineplus.cineplus.persistance.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> { }