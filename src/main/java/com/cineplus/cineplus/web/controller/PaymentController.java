package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.entity.PaymentTransaction;
import com.cineplus.cineplus.domain.service.PaymentService;
import com.cineplus.cineplus.domain.service.PaymentProcessorService;
import com.cineplus.cineplus.domain.service.SmsNotificationService;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentProcessorService paymentProcessorService;
    private final SmsNotificationService smsNotificationService;

    @PostMapping("/sandbox")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sandboxPay(@RequestBody SandboxPaymentRequest request) {
        PaymentTransaction tx = paymentService.processSandboxPayment(
                request.getOrderId(),
                request.getPaymentMethodId(),
                request.getAmount(),
                request.getCurrency(),
                request.getNote()
        );

        return ResponseEntity.ok(Map.of(
                "status", tx.getStatus(),
                "reference", tx.getReference(),
                "transactionId", tx.getId(),
                "orderId", tx.getOrder() != null ? tx.getOrder().getId() : null
        ));
    }

    /**
     * Endpoint para procesar pagos con validación de tarjeta y simulación local.
     * POST /api/payments/process
     * Body: {
     *   "amount": 99.99,
     *   "cardNumber": "4111111111111111",
     *   "cardHolder": "JUAN PEREZ",
     *   "expiryDate": "12/25",
     *   "cvv": "123",
     *   "phoneNumber": "912345678"
     * }
     */
    @PostMapping("/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> processPayment(@RequestBody PaymentProcessRequest request) {
        try {
            // 1. Validar datos de la tarjeta
            if (!paymentProcessorService.validateCardData(request.getCardNumber(), request.getCvv(), request.getExpiryDate())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Datos de tarjeta inválidos",
                    "error", "INVALID_CARD_DATA"
                ));
            }

            // 2. Procesar el pago
            PaymentProcessorService.PaymentResult result = paymentProcessorService.processPayment(
                System.currentTimeMillis(),
                request.getAmount(),
                request.getCardNumber().substring(request.getCardNumber().length() - 4)
            );

            if (!result.success) {
                System.out.println("[PAYMENT] ✗ Pago rechazado: " + result.message);
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(Map.of(
                    "success", false,
                    "message", result.message,
                    "authCode", result.authorizationCode
                ));
            }

            // 3. Si es exitoso, enviar SMS de confirmación
            if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
                smsNotificationService.sendOrderConfirmationSms(
                    request.getPhoneNumber(),
                    System.currentTimeMillis(),
                    request.getAmount()
                );
            }

            System.out.println("[PAYMENT] ✓ Pago procesado exitosamente");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionCode", result.transactionCode,
                "authorizationCode", result.authorizationCode,
                "amount", request.getAmount(),
                "processedAt", result.processedAt
            ));

        } catch (Exception e) {
            System.err.println("[PAYMENT-ERROR] " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al procesar el pago",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para procesar reembolsos.
     * POST /api/payments/refund/{transactionId}
     */
    @PostMapping("/refund/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> processRefund(@PathVariable Long transactionId) {
        try {
            PaymentProcessorService.PaymentResult result = paymentProcessorService.processRefund(transactionId);

            if (!result.success) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", result.message
                ));
            }

            System.out.println("[REFUND] ✓ Reembolso procesado: " + transactionId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionCode", result.transactionCode,
                "message", "Reembolso procesado exitosamente"
            ));

        } catch (Exception e) {
            System.err.println("[REFUND-ERROR] " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al procesar el reembolso"
            ));
        }
    }

    @Data
    public static class SandboxPaymentRequest {
        private Long orderId;
        private Long paymentMethodId;
        @NotNull
        private BigDecimal amount;
        private String currency = "PEN";
        private String note;
    }

    @Data
    public static class PaymentProcessRequest {
        @NotNull
        private BigDecimal amount;
        @NotNull
        private String cardNumber;
        @NotNull
        private String cardHolder;
        @NotNull
        private String expiryDate;  // MM/YY
        @NotNull
        private String cvv;
        private String phoneNumber;  // Opcional, para SMS
    }
}
