package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Movie;
import com.cineplus.cineplus.domain.entity.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
	Page<Movie> findByStatus(MovieStatus status, Pageable pageable);
	Page<Movie> findByGenreIgnoreCase(String genre, Pageable pageable);
	Page<Movie> findByStatusAndGenreIgnoreCase(MovieStatus status, String genre, Pageable pageable);
	Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
	Page<Movie> findByStatusAndTitleContainingIgnoreCase(MovieStatus status, String title, Pageable pageable);
	Page<Movie> findByStatusAndGenreIgnoreCaseAndTitleContainingIgnoreCase(MovieStatus status, String genre, String title, Pageable pageable);
}