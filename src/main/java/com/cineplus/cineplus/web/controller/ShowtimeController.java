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

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    // GET /api/showtimes?cinema={id}&movie={id}
    @GetMapping
    public ResponseEntity<List<ShowtimeDto>> getShowtimeDatesOrDetails(
            @RequestParam @NotNull Long cinema,
            @RequestParam @NotNull Long movie,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) FormatType format) {

        if (date == null) {
            // Si no se especifica fecha, se asume que se buscan las fechas disponibles (getAvailableShowtimeDates)
            // Nota: Este método en el servicio devuelve DTOs forzados.
            // Considerar un DTO más simple si solo se necesitan las fechas.
            return ResponseEntity.ok(showtimeService.getAvailableShowtimeDates(cinema, movie));
        } else if (format != null) {
            // Si se especifica fecha y formato, se buscan los horarios específicos
            return ResponseEntity.ok(showtimeService.getMovieShowtimes(cinema, movie, date, format));
        } else {
            // Si solo se especifica fecha, podríamos devolver todos los formatos para esa fecha,
            // pero por el momento el servicio requiere un formato.
            // Se puede extender esta lógica si es necesario en el futuro.
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

        ShowtimeDto createdShowtime = showtimeDto;

        createdShowtime = showtimeService.saveShowtime(showtimeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShowtime);
    }

}