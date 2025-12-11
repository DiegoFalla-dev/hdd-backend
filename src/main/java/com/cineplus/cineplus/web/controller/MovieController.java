package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.MovieDto;
import com.cineplus.cineplus.domain.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import com.cineplus.cineplus.domain.entity.MovieStatus;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<?> getMovies(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {

        boolean anyFilter = status != null || genre != null || query != null;
        if (!anyFilter && page == 0) { // original simple list
            List<MovieDto> movies = movieService.findAllMovies();
            return ResponseEntity.ok(movies);
        }

        MovieStatus statusEnum = status != null ? MovieStatus.forValue(status) : null;
        Page<MovieDto> result = movieService.searchMovies(statusEnum, genre, query, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/now-playing")
    public ResponseEntity<List<MovieDto>> getNowPlaying() {
        List<MovieDto> movies = movieService.findByStatus(MovieStatus.CARTELERA);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MovieDto>> getUpcoming() {
        List<MovieDto> movies = movieService.findByStatus(MovieStatus.PROXIMO);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/presale")
    public ResponseEntity<List<MovieDto>> getPresale() {
        List<MovieDto> movies = movieService.findByStatus(MovieStatus.PREVENTA);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        return movieService.findMovieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieDto movieDto) {
        MovieDto createdMovie = movieService.saveMovie(movieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieDto movieDto) {
        // En una actualización, el ID del DTO podría no coincidir con el PathVariable.
        // Aseguramos que el ID correcto sea el del PathVariable.
        movieDto.setId(id);
        return movieService.findMovieById(id)
                .map(existingMovie -> ResponseEntity.ok(movieService.saveMovie(movieDto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        if (movieService.findMovieById(id).isPresent()) {
            movieService.deleteMovie(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}