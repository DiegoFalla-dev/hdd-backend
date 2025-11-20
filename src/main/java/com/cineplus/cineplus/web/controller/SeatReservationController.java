package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.ConfirmPurchaseDto;
import com.cineplus.cineplus.domain.dto.ReserveSeatRequestDto;
import com.cineplus.cineplus.domain.dto.SeatDto;
import com.cineplus.cineplus.domain.entity.Seat;
import com.cineplus.cineplus.domain.repository.SeatRepository;
import com.cineplus.cineplus.domain.service.SeatReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar reservas de asientos con sistema de sesiones.
 * 
 * Endpoints principales:
 * - POST   /api/seat-reservations/{showtimeId}         - Iniciar reserva de asientos
 * - DELETE /api/seat-reservations/{sessionId}          - Liberar reserva por sessionId
 * - POST   /api/seat-reservations/confirm              - Confirmar compra
 * - POST   /api/seat-reservations/cancel               - Cancelar asientos permanentemente
 * - POST   /api/seat-reservations/release-occupied     - Liberar asientos ocupados
 * - GET    /api/seat-reservations/{sessionId}/seats    - Obtener asientos de una sesión
 * - GET    /api/seat-reservations/{showtimeId}/matrix  - Obtener matriz de asientos
 */
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/seat-reservations")
@RequiredArgsConstructor
public class SeatReservationController {

    private final SeatReservationService seatReservationService;
    private final SeatRepository seatRepository;

    /**
     * Inicia una nueva reserva de asientos.
     * Retorna el sessionId que debe usarse para confirmar o liberar la reserva.
     * Los asientos quedan en estado TEMPORARILY_RESERVED por 1 minuto.
     * 
     * POST /api/seat-reservations/{showtimeId}
     * Body: { "seatIdentifiers": ["A1", "A2"], "userId": 123 }
     * 
     * @param showtimeId ID del showtime
     * @param request Cuerpo con identificadores de asientos y userId opcional
     * @return sessionId generado
     */
    @PostMapping("/{showtimeId}")
    public ResponseEntity<Map<String, String>> initiateReservation(
            @PathVariable Long showtimeId,
            @RequestBody ReserveSeatRequestDto request) {
        
        String sessionId = seatReservationService.initiateSeatReservation(
                showtimeId, request.getSeatIdentifiers(), request.getUserId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("sessionId", sessionId, 
                            "message", "Seats reserved temporarily for 1 minute"));
    }

    /**
     * Libera manualmente los asientos reservados en una sesión.
     * Solo libera asientos que NO estén permanentemente cancelados.
     * 
     * DELETE /api/seat-reservations/{sessionId}
     * 
     * @param sessionId ID de la sesión a liberar
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> releaseReservation(@PathVariable String sessionId) {
        seatReservationService.releaseReservationBySession(sessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Confirma la compra de los asientos reservados.
     * Cambia el estado de TEMPORARILY_RESERVED a OCCUPIED.
     * Asocia un número de orden/compra a los asientos.
     * 
     * POST /api/seat-reservations/confirm
     * Body: { "sessionId": "uuid", "purchaseNumber": "ORD-12345" }
     * 
     * @param confirmDto Datos de confirmación
     */
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmPurchase(
            @RequestBody ConfirmPurchaseDto confirmDto) {
        
        seatReservationService.confirmReservation(
                confirmDto.getSessionId(), confirmDto.getPurchaseNumber());
        
        return ResponseEntity.ok(Map.of("message", "Purchase confirmed successfully"));
    }

    /**
     * Cancela permanentemente un grupo de asientos.
     * Los asientos quedan en estado CANCELLED y no pueden ser reservados nuevamente.
     * Requiere un número de orden asociado a la cancelación.
     * 
     * POST /api/seat-reservations/cancel/{showtimeId}
     * Body: { "seatIdentifiers": ["A1", "A2"], "purchaseNumber": "ORD-12345" }
     * 
     * @param showtimeId ID del showtime
     * @param request Datos de cancelación
     */
    @PostMapping("/cancel/{showtimeId}")
    public ResponseEntity<Map<String, String>> cancelSeatsPermanently(
            @PathVariable Long showtimeId,
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        Set<String> seatIdentifiers = ((List<String>) request.get("seatIdentifiers"))
                .stream().collect(Collectors.toSet());
        String purchaseNumber = (String) request.get("purchaseNumber");
        
        seatReservationService.cancelSeatsPermanently(showtimeId, seatIdentifiers, purchaseNumber);
        
        return ResponseEntity.ok(Map.of("message", "Seats cancelled permanently"));
    }

    /**
     * Libera asientos que están en estado OCCUPIED.
     * Solo libera los que NO están permanentemente cancelados.
     * Útil para cancelaciones de compra o devoluciones.
     * 
     * POST /api/seat-reservations/release-occupied/{showtimeId}
     * Body: { "seatIdentifiers": ["A1", "A2"] }
     * 
     * @param showtimeId ID del showtime
     * @param request Datos con identificadores de asientos
     */
    @PostMapping("/release-occupied/{showtimeId}")
    public ResponseEntity<Map<String, String>> releaseOccupiedSeats(
            @PathVariable Long showtimeId,
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        Set<String> seatIdentifiers = ((List<String>) request.get("seatIdentifiers"))
                .stream().collect(Collectors.toSet());
        
        seatReservationService.releaseOccupiedSeats(showtimeId, seatIdentifiers);
        
        return ResponseEntity.ok(Map.of("message", "Occupied seats released successfully"));
    }

    /**
     * Obtiene la lista de asientos reservados en una sesión específica.
     * 
     * GET /api/seat-reservations/{sessionId}/seats
     * 
     * @param sessionId ID de la sesión
     * @return Lista de identificadores de asientos
     */
    @GetMapping("/{sessionId}/seats")
    public ResponseEntity<List<String>> getReservedSeats(@PathVariable String sessionId) {
        List<String> seats = seatReservationService.getReservedSeatsBySession(sessionId);
        return ResponseEntity.ok(seats);
    }

    /**
     * Obtiene la matriz completa de asientos de un showtime con sus coordenadas.
     * Incluye el estado de cada asiento y si está cancelado permanentemente.
     * 
     * GET /api/seat-reservations/{showtimeId}/matrix
     * 
     * @param showtimeId ID del showtime
     * @return Lista de todos los asientos con sus detalles
     */
    @GetMapping("/{showtimeId}/matrix")
    public ResponseEntity<List<SeatDto>> getSeatMatrix(@PathVariable Long showtimeId) {
        List<Seat> seats = seatRepository.findByShowtimeId(showtimeId);
        
        List<SeatDto> seatDtos = seats.stream()
                .map(seat -> new SeatDto(
                        seat.getId(),
                        seat.getSeatIdentifier(),
                        seat.getStatus(),
                        seat.getRowPosition(),
                        seat.getColPosition(),
                        seat.getIsCancelled(),
                        seat.getSessionId(),
                        seat.getPurchaseNumber()
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(seatDtos);
    }
}
