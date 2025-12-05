package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.MovieDto;
import com.cineplus.cineplus.domain.entity.MovieStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<MovieDto> findAllMovies();
    Optional<MovieDto> findMovieById(Long id);
    MovieDto saveMovie(MovieDto movieDto);
    void deleteMovie(Long id);

    /**
     * Filtered and paginated search combining optional status, genre and a free-text query (title).
     */
    Page<MovieDto> searchMovies(MovieStatus status,
                                String genre,
                                String query,
                                int page,
                                int size);

    List<MovieDto> findByStatus(MovieStatus status);
}