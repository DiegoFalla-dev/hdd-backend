package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.CinemaDto;
import com.cineplus.cineplus.domain.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    @GetMapping
    public ResponseEntity<List<CinemaDto>> getAllCinemas() {
        List<CinemaDto> cinemas = cinemaService.findAllCinemas();
        return ResponseEntity.ok(cinemas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaDto> getCinemaById(@PathVariable Long id) {
        return cinemaService.findCinemaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CinemaDto> createCinema(@RequestBody CinemaDto cinemaDto) {
        CinemaDto createdCinema = cinemaService.saveCinema(cinemaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCinema);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CinemaDto> updateCinema(@PathVariable Long id, @RequestBody CinemaDto cinemaDto) {
        cinemaDto.setId(id);
        return cinemaService.findCinemaById(id)
                .map(existingCinema -> ResponseEntity.ok(cinemaService.saveCinema(cinemaDto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCinema(@PathVariable Long id) {
        if (cinemaService.findCinemaById(id).isPresent()) {
            cinemaService.deleteCinema(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}