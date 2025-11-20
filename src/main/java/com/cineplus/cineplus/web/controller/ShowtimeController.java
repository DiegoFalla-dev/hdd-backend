package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.ShowtimeDto;
import com.cineplus.cineplus.domain.entity.Showtime.FormatType;
import com.cineplus.cineplus.domain.service.ShowtimeService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Controlador REST para gestionar horarios de funciones (showtimes)
 * 
 * IMPORTANTE: Este endpoint permite solicitudes desde el frontend en:
 * - http://localhost:5173 (Vite dev server - puerto principal)
 * - http://localhost:5174 (Vite dev server - puerto alternativo)
 * 
 * Si el frontend cambia de puerto o se despliega en producción,
 * actualizar las URLs en @CrossOrigin y en SecurityConfig.java
 */
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    // GET /api/showtimes - Endpoint principal para el frontend (nuevo)
    // Busca showtimes con filtros opcionales: movieId (requerido), cinemaId y date
    @GetMapping
    public ResponseEntity<List<ShowtimeDto>> getShowtimes(
            @RequestParam @NotNull Long movieId,
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<ShowtimeDto> showtimes = showtimeService.findShowtimes(movieId, cinemaId, date);
        return ResponseEntity.ok(showtimes);
    }

    // GET /api/showtimes/legacy - Endpoint legacy mantenido por compatibilidad
    @GetMapping("/legacy")
    public ResponseEntity<List<ShowtimeDto>> getShowtimeDatesOrDetailsLegacy(
            @RequestParam @NotNull Long cinema,
            @RequestParam @NotNull Long movie,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) FormatType format) {

        if (date == null) {
            return ResponseEntity.ok(showtimeService.getAvailableShowtimeDates(cinema, movie));
        } else if (format != null) {
            return ResponseEntity.ok(showtimeService.getMovieShowtimes(cinema, movie, date, format));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/showtimes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeDto> getShowtimeDetailsById(
            @PathVariable Long id,
            @RequestParam @NotNull Long cinema) { // Requiere el cinemaId para verificar que el showtime pertenece a ese cine
        return showtimeService.getShowtimeDetails(id, cinema)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/showtimes/{id}/seats/generate (Endpoint para generar asientos iniciales)
    @PostMapping("/{id}/seats/generate")
    public ResponseEntity<Void> generateSeats(@PathVariable Long id) {
        showtimeService.generateSeatsForShowtime(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    /**
     * Endpoint para generar asientos automáticamente para todos los showtimes que no tienen asientos.
     * Útil para inicializar asientos de funciones creadas antes de implementar la auto-generación.
     * 
     * POST /api/showtimes/seats/generate-all
     * 
     * @return Cantidad de showtimes a los que se les generaron asientos
     */
    @PostMapping("/seats/generate-all")
    public ResponseEntity<String> generateSeatsForAll() {
        int generatedCount = showtimeService.generateSeatsForAllShowtimesWithoutSeats();
        return ResponseEntity.ok("Se generaron asientos para " + generatedCount + " funciones");
    }

    // GET /api/showtimes/{id}/seats/occupied
    @GetMapping("/{id}/seats/occupied")
    public ResponseEntity<List<String>> getOccupiedSeats(@PathVariable Long id) {
        List<String> occupiedSeats = showtimeService.getOccupiedSeats(id);
        return ResponseEntity.ok(occupiedSeats);
    }

    // POST /api/showtimes/{id}/seats/reserve (Reservar asientos temporalmente)
    @PostMapping("/{id}/seats/reserve")
    public ResponseEntity<List<String>> reserveSeats(@PathVariable Long id, @RequestBody Set<String> seatIdentifiers) {
        List<String> failedReservations = showtimeService.reserveSeatsTemporarily(id, seatIdentifiers);
        if (failedReservations.isEmpty()) {
            return ResponseEntity.ok(failedReservations); // Todas las reservas exitosas
        } else {
            // Algunas reservas fallaron, se devuelve la lista de las que no se pudieron reservar.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(failedReservations);
        }
    }

    // POST /api/showtimes/{id}/seats/release (Liberar asientos temporalmente reservados)
    @PostMapping("/{id}/seats/release")
    public ResponseEntity<Void> releaseSeats(@PathVariable Long id, @RequestBody Set<String> seatIdentifiers) {
        showtimeService.releaseTemporaryReservedSeats(id, seatIdentifiers);
        return ResponseEntity.noContent().build();
    }

    // POST /api/showtimes/{id}/seats/confirm (Confirmar asientos como ocupados)
    @PostMapping("/{id}/seats/confirm")
    public ResponseEntity<Void> confirmSeats(@PathVariable Long id, @RequestBody Set<String> seatIdentifiers) {
        showtimeService.confirmSeatsAsOccupied(id, seatIdentifiers);
        return ResponseEntity.noContent().build();
    }

    // POST /api/showtimes
    @PostMapping
    public ResponseEntity<ShowtimeDto> createShowtime(@RequestBody ShowtimeDto showtimeDto) {
        ShowtimeDto createdShowtime = showtimeService.saveShowtime(showtimeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShowtime);
    }

}