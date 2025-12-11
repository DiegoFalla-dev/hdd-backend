package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.entity.PaymentTransaction;
import com.cineplus.cineplus.domain.service.PaymentService;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

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

    @Data
    public static class SandboxPaymentRequest {
        private Long orderId;
        private Long paymentMethodId;
        @NotNull
        private BigDecimal amount;
        private String currency = "PEN";
        private String note;
    }
}
