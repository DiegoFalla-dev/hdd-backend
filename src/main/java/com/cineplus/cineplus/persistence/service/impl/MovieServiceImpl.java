package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.MovieDto;
import com.cineplus.cineplus.domain.entity.Movie;
import com.cineplus.cineplus.domain.entity.MovieStatus;
import com.cineplus.cineplus.domain.repository.MovieRepository;
import com.cineplus.cineplus.domain.service.MovieService;
import com.cineplus.cineplus.persistence.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> findAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MovieDto> findMovieById(Long id) {
        return movieRepository.findById(id)
                .map(movieMapper::toDto);
    }

    @Override
    @Transactional
    public MovieDto saveMovie(MovieDto movieDto) {
        Movie movie = movieMapper.toEntity(movieDto);
        Movie savedMovie = movieRepository.save(movie);
        return movieMapper.toDto(savedMovie);
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieDto> searchMovies(MovieStatus status, String genre, String query, int page, int size) {
        final Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        final String g = (genre == null || genre.isBlank()) ? null : genre;
        final String q = (query == null || query.isBlank()) ? null : query;

        Page<Movie> result;
        boolean hasStatus = status != null;
        boolean hasGenre = g != null;
        boolean hasQuery = q != null;

        if (hasStatus && hasGenre && hasQuery) {
            result = movieRepository.findByStatusAndGenreIgnoreCaseAndTitleContainingIgnoreCase(status, g, q, pageable);
        } else if (hasStatus && hasGenre) {
            result = movieRepository.findByStatusAndGenreIgnoreCase(status, g, pageable);
        } else if (hasStatus && hasQuery) {
            result = movieRepository.findByStatusAndTitleContainingIgnoreCase(status, q, pageable);
        } else if (hasGenre && hasQuery) {
            Page<Movie> genrePage = movieRepository.findByGenreIgnoreCase(g, pageable);
                List<Movie> filtered = genrePage.getContent().stream()
                    .filter(m -> q != null && m.getTitle() != null && m.getTitle().toLowerCase().contains(q.toLowerCase()))
                    .collect(Collectors.toList());
            result = new PageImpl<>(filtered, pageable, filtered.size());
        } else if (hasStatus) {
            result = movieRepository.findByStatus(status, pageable);
        } else if (hasGenre) {
            result = movieRepository.findByGenreIgnoreCase(g, pageable);
        } else if (hasQuery) {
            result = movieRepository.findByTitleContainingIgnoreCase(q, pageable);
        } else {
            result = movieRepository.findAll(pageable);
        }
        return result.map(movieMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDto> findByStatus(MovieStatus status) {
        return movieRepository.findByStatus(status, PageRequest.of(0, 100)).stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }
}