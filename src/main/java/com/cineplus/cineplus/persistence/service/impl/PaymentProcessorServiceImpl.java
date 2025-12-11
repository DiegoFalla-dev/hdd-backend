package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.service.PaymentProcessorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PaymentProcessorServiceImpl implements PaymentProcessorService {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessorServiceImpl.class);
    private static final Random random = new Random();
    
    // Tarjetas de prueba (simuladas)
    private static final String TEST_CARD_SUCCESS = "4111111111111111";
    private static final String TEST_CARD_DECLINED = "4222222222222220";
    private static final String TEST_CARD_INSUFFICIENT = "4333333333333333";

    @Override
    public PaymentResult processPayment(Long paymentTransactionId, BigDecimal amount, String cardLastFourDigits) {
        try {
            log.info("[PAYMENT] Procesando pago | Transacción: {} | Monto: S/{} | Tarjeta: ****{}", 
                    paymentTransactionId, amount, cardLastFourDigits);

            // Simular diferentes escenarios basados en el monto
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("[PAYMENT] Monto inválido: {}", amount);
                return new PaymentResult(
                    false,
                    generateTransactionCode(paymentTransactionId),
                    "Monto inválido",
                    null
                );
            }

            // Simular rechazo ocasional (5% de probabilidad)
            if (random.nextInt(100) < 5) {
                log.warn("[PAYMENT] Pago rechazado (simulado) | Transacción: {}", paymentTransactionId);
                return new PaymentResult(
                    false,
                    generateTransactionCode(paymentTransactionId),
                    "Fondos insuficientes o tarjeta rechazada",
                    null
                );
            }

            // Simulación exitosa
            String transactionCode = generateTransactionCode(paymentTransactionId);
            String authCode = generateAuthorizationCode();

            log.info("[PAYMENT] ✓ Pago procesado exitosamente | Código: {} | Auth: {}", 
                    transactionCode, authCode);

            return new PaymentResult(
                true,
                transactionCode,
                "Pago procesado correctamente",
                authCode
            );

        } catch (Exception e) {
            log.error("[PAYMENT] Error procesando pago: {}", e.getMessage());
            return new PaymentResult(
                false,
                generateTransactionCode(paymentTransactionId),
                "Error procesando pago: " + e.getMessage(),
                null
            );
        }
    }

    @Override
    public PaymentResult processRefund(Long paymentTransactionId) {
        try {
            log.info("[REFUND] Procesando reembolso | Transacción: {}", paymentTransactionId);

            // Simular reembolso exitoso
            String transactionCode = "REFUND-" + paymentTransactionId + "-" + 
                                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String authCode = generateAuthorizationCode();

            log.info("[REFUND] ✓ Reembolso procesado | Código: {} | Auth: {}", transactionCode, authCode);

            return new PaymentResult(
                true,
                transactionCode,
                "Reembolso procesado correctamente",
                authCode
            );

        } catch (Exception e) {
            log.error("[REFUND] Error procesando reembolso: {}", e.getMessage());
            return new PaymentResult(
                false,
                "REFUND-" + paymentTransactionId,
                "Error procesando reembolso: " + e.getMessage(),
                null
            );
        }
    }

    @Override
    public boolean validateCardData(String cardNumber, String cvv, String expiryDate) {
        if (cardNumber == null || cvv == null || expiryDate == null) {
            return false;
        }

        // Validar formato de tarjeta (Luhn algorithm simplificado)
        if (!isValidCardNumber(cardNumber)) {
            log.warn("[VALIDATION] Número de tarjeta inválido");
            return false;
        }

        // Validar CVV (3-4 dígitos)
        if (!Pattern.matches("^\\d{3,4}$", cvv)) {
            log.warn("[VALIDATION] CVV inválido");
            return false;
        }

        // Validar fecha de expiración (MM/YY)
        if (!Pattern.matches("^(0[1-9]|1[0-2])/\\d{2}$", expiryDate)) {
            log.warn("[VALIDATION] Fecha de expiración inválida");
            return false;
        }

        // Verificar que no esté expirada
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);
        LocalDateTime expiry = LocalDateTime.of(year, month, 28, 23, 59);

        if (LocalDateTime.now().isAfter(expiry)) {
            log.warn("[VALIDATION] Tarjeta expirada");
            return false;
        }

        log.info("[VALIDATION] ✓ Datos de tarjeta válidos");
        return true;
    }

    private String generateTransactionCode(Long paymentTransactionId) {
        return "TXN-" + paymentTransactionId + "-" + 
               LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String generateAuthorizationCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * Validar número de tarjeta usando algoritmo de Luhn simplificado
     */
    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || !Pattern.matches("^\\d{13,19}$", cardNumber)) {
            return false;
        }

        // Algoritmo de Luhn
        int sum = 0;
        boolean isEven = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (isEven) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            isEven = !isEven;
        }

        return (sum % 10) == 0;
    }
}
