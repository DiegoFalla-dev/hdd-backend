package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.CreatePurchaseDto;
import com.cineplus.cineplus.domain.dto.PaymentResponseDto;
import com.cineplus.cineplus.domain.dto.PurchaseDto;
import com.cineplus.cineplus.domain.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controlador REST para gestionar compras y pagos
 * 
 * Endpoints:
 * - POST   /api/payments/process                - Procesar pago y crear compra
 * - GET    /api/users/{userId}/purchases        - Historial de compras del usuario
 * - GET    /api/purchases/{purchaseNumber}      - Detalle de una compra específica
 */
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * Procesa un pago completo:
     * 1. Valida sesión de reserva
     * 2. Valida método de pago
     * 3. Procesa el pago (simulado por ahora)
     * 4. Crea registro de compra
     * 5. Confirma asientos como OCCUPIED
     * 
     * POST /api/payments/process
     * 
     * Body:
     * {
     *   "sessionId": "uuid-aqui",
     *   "userId": 123,
     *   "paymentMethodId": 1,
     *   "amount": 75.00,
     *   "items": [
     *     {
     *       "type": "TICKET",
     *       "description": "Entrada General - Los 4 Fantásticos",
     *       "quantity": 3,
     *       "unitPrice": 25.00,
     *       "seatIdentifiers": "A1,A2,A3"
     *     }
     *   ]
     * }
     * 
     * Response:
     * {
     *   "success": true,
     *   "purchaseNumber": "CIN-20251120153045-A7B3C9D1",
     *   "transactionId": "TXN-98765432",
     *   "message": "Payment processed successfully"
     * }
     */
    @PostMapping("/payments/process")
    public ResponseEntity<PaymentResponseDto> processPurchase(
            @Valid @RequestBody CreatePurchaseDto request) {
        
        log.info("Received payment processing request for sessionId: {}", request.getSessionId());
        
        try {
            PaymentResponseDto response = purchaseService.processPurchase(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResponseStatusException e) {
            log.error("Payment processing failed: {}", e.getReason());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing payment", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred while processing payment");
        }
    }

    /**
     * Obtiene el historial de compras de un usuario
     * 
     * GET /api/users/{userId}/purchases
     * 
     * Response:
     * [
     *   {
     *     "id": 1,
     *     "purchaseNumber": "CIN-20251120153045-A7B3C9D1",
     *     "movieTitle": "Los 4 Fantásticos",
     *     "cinemaName": "Cineplus Jockey Plaza",
     *     "showtimeDate": "2025-11-20",
     *     "showtimeTime": "14:00",
     *     "totalAmount": 75.00,
     *     "purchaseDate": "2025-11-20T15:30:45",
     *     "status": "COMPLETED"
     *   }
     * ]
     */
    @GetMapping("/users/{userId}/purchases")
    public ResponseEntity<List<PurchaseDto>> getUserPurchases(@PathVariable Long userId) {
        log.info("Fetching purchases for user: {}", userId);
        
        List<PurchaseDto> purchases = purchaseService.getUserPurchases(userId);
        return ResponseEntity.ok(purchases);
    }

    /**
     * Obtiene el detalle completo de una compra por su número
     * 
     * GET /api/purchases/{purchaseNumber}
     * 
     * Response:
     * {
     *   "purchaseNumber": "CIN-20251120153045-A7B3C9D1",
     *   "user": {
     *     "id": 123,
     *     "name": "Juan Pérez",
     *     "email": "juan@example.com"
     *   },
     *   "showtime": {
     *     "movieTitle": "Los 4 Fantásticos",
     *     "cinemaName": "Cineplus Jockey Plaza",
     *     "date": "2025-11-20",
     *     "time": "14:00"
     *   },
     *   "items": [...],
     *   "totalAmount": 75.00,
     *   "status": "COMPLETED"
     * }
     */
    @GetMapping("/purchases/{purchaseNumber}")
    public ResponseEntity<PurchaseDto> getPurchaseDetails(@PathVariable String purchaseNumber) {
        log.info("Fetching purchase details for: {}", purchaseNumber);
        
        return purchaseService.getPurchaseByNumber(purchaseNumber)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Purchase not found with number: " + purchaseNumber));
    }
}
