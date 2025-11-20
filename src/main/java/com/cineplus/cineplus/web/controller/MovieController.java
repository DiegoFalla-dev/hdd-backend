package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.MovieDto;
import com.cineplus.cineplus.domain.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar películas
 * 
 * IMPORTANTE: Este endpoint permite solicitudes desde el frontend en:
 * - http://localhost:5173 (Vite dev server - puerto principal)
 * - http://localhost:5174 (Vite dev server - puerto alternativo)
 * 
 * Si el frontend cambia de puerto o se despliega en producción,
 * actualizar las URLs en @CrossOrigin y en SecurityConfig.java
 */
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "https://hdd-frontend.onrender.com"})
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<MovieDto> movies = movieService.findAllMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        return movieService.findMovieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MovieDto> createMovie(@RequestBody MovieDto movieDto) {
        MovieDto createdMovie = movieService.saveMovie(movieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(@PathVariable Long id, @RequestBody MovieDto movieDto) {
        // En una actualización, el ID del DTO podría no coincidir con el PathVariable.
        // Aseguramos que el ID correcto sea el del PathVariable.
        movieDto.setId(id);
        return movieService.findMovieById(id)
                .map(existingMovie -> ResponseEntity.ok(movieService.saveMovie(movieDto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        if (movieService.findMovieById(id).isPresent()) {
            movieService.deleteMovie(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}