package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.SeatDto;
import com.cineplus.cineplus.domain.dto.ShowtimeDto;
import com.cineplus.cineplus.domain.entity.Showtime.FormatType;
import com.cineplus.cineplus.domain.service.ShowtimeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

// @CrossOrigin removed, now global CORS config is used
@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    // GET /api/showtimes?cinema={id}&movie={id}
    @GetMapping
    public ResponseEntity<List<ShowtimeDto>> getShowtimeDatesOrDetails(
            @RequestParam @NotNull Long cinema,
            @RequestParam(required = false) Long movie,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) FormatType format) {

        // Si no se especifica movie, devolver todas las funciones del cine
        if (movie == null) {
            return ResponseEntity.ok(showtimeService.getShowtimesByCinema(cinema));
        }

        if (date == null) {
            // Si no se especifica fecha, se asume que se buscan las fechas disponibles (getAvailableShowtimeDates)
            // Nota: Este método en el servicio devuelve DTOs forzados.
            // Considerar un DTO más simple si solo se necesitan las fechas.
            return ResponseEntity.ok(showtimeService.getAvailableShowtimeDates(cinema, movie));
        } else if (format != null) {
            // Si se especifica fecha y formato, se buscan los horarios específicos
            return ResponseEntity.ok(showtimeService.getMovieShowtimes(cinema, movie, date, format));
        } else {
            // Si se especifica fecha pero no formato, devolver todas las funciones del día (todos los formatos)
            // para que el frontend pueda extraer los formatos disponibles y los horarios.
            return ResponseEntity.ok(showtimeService.getShowtimesByDate(cinema, movie, date));
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
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Void> generateSeats(@PathVariable Long id) {
        showtimeService.generateSeatsForShowtime(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // GET /api/showtimes/{id}/seats (Obtener todos los asientos de una función)
    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatDto>> getSeats(@PathVariable Long id) {
        List<SeatDto> seats = showtimeService.getSeatsByShowtime(id);
        return ResponseEntity.ok(seats);
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
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ShowtimeDto> createShowtime(@Valid @RequestBody ShowtimeDto showtimeDto) {
        ShowtimeDto createdShowtime = showtimeService.saveShowtime(showtimeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShowtime);
    }

    // PUT /api/showtimes/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ShowtimeDto> updateShowtime(@PathVariable Long id, @Valid @RequestBody ShowtimeDto showtimeDto) {
        showtimeDto.setId(id);
        ShowtimeDto updatedShowtime = showtimeService.updateShowtime(showtimeDto);
        return ResponseEntity.ok(updatedShowtime);
    }

    // DELETE /api/showtimes/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.noContent().build();
    }

}