package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.TheaterDto;
import com.cineplus.cineplus.domain.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar salas/theaters
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
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @GetMapping
    public ResponseEntity<List<TheaterDto>> getAllTheaters(@RequestParam(required = false) Long cinemaId) {
        if (cinemaId != null) {
            List<TheaterDto> theaters = theaterService.findTheatersByCinemaId(cinemaId);
            return ResponseEntity.ok(theaters);
        } else {
            List<TheaterDto> theaters = theaterService.findAllTheaters();
            return ResponseEntity.ok(theaters);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheaterDto> getTheaterById(@PathVariable Long id) {
        return theaterService.findTheaterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TheaterDto> createTheater(@RequestBody TheaterDto theaterDto) {
        TheaterDto createdTheater = theaterService.saveTheater(theaterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTheater);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TheaterDto> updateTheater(@PathVariable Long id, @RequestBody TheaterDto theaterDto) {
        theaterDto.setId(id); // Asegura que el ID del DTO coincida con el PathVariable
        return theaterService.findTheaterById(id)
                .map(existingTheater -> ResponseEntity.ok(theaterService.saveTheater(theaterDto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long id) {
        if (theaterService.findTheaterById(id).isPresent()) {
            theaterService.deleteTheater(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}