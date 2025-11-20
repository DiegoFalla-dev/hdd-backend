package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.ReservationResponseDto;
import com.cineplus.cineplus.domain.dto.SeatDto;
import com.cineplus.cineplus.domain.service.SeatReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/showtimes/{showtimeId}/seats")
public class SeatController {

    private final SeatReservationService seatService;

    public SeatController(SeatReservationService seatService) {
        this.seatService = seatService;
    }

    @GetMapping
    public ResponseEntity<List<SeatDto>> getSeats(@PathVariable Long showtimeId,
                                                  @RequestParam(defaultValue = "false") boolean includeHoldInfo) {
        List<SeatDto> seats = seatService.getSeatsForShowtime(showtimeId, includeHoldInfo);
        return ResponseEntity.ok(seats);
    }

    public static class ReserveRequest {
        public List<Long> seatIds;
        public Long userId;
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponseDto> reserve(@PathVariable Long showtimeId, @RequestBody ReserveRequest req) {
        try {
            ReservationResponseDto resp = seatService.reserveSeats(showtimeId, req.seatIds, req.userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Reservation failed");
        }
    }

    public static class ConfirmRequest {
        public Long reservationId;
        public String token;
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@PathVariable Long showtimeId, @RequestBody ConfirmRequest req) {
        try {
            Long orderId = seatService.confirmReservation(showtimeId, req.reservationId, req.token);
            return ResponseEntity.ok().body(java.util.Map.of("orderId", orderId, "status", "CONFIRMED"));
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Confirm failed");
        }
    }

    @DeleteMapping("/reserve/{reservationId}")
    public ResponseEntity<?> cancel(@PathVariable Long showtimeId, @PathVariable Long reservationId) {
        try {
            seatService.cancelReservation(showtimeId, reservationId);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cancel failed");
        }
    }
}
