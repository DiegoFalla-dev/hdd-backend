package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.MovieDto;
import java.util.List;
import java.util.Optional;

public interface MovieService {
    List<MovieDto> findAllMovies();
    Optional<MovieDto> findMovieById(Long id);
    MovieDto saveMovie(MovieDto movieDto);
    void deleteMovie(Long id);
}