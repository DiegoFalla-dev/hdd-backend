package com.cineplus.cineplus.domain.service;

/**
 * Servicio de procesamiento de pagos SIMULADO (sin APIs externas).
 * Valida y procesa pagos como si fueran reales pero de forma local.
 */
public interface PaymentProcessorService {

    /**
     * Procesa un pago simulando una pasarela real (Culqi/Niubiz).
     * @param paymentTransactionId ID de la transacción a procesar
     * @param amount Monto en soles
     * @param cardLastFourDigits Últimos 4 dígitos de la tarjeta
     * @return PaymentResult con detalles del procesamiento
     */
    PaymentResult processPayment(Long paymentTransactionId, java.math.BigDecimal amount, String cardLastFourDigits);

    /**
     * Procesa un reembolso simulado.
     * @param paymentTransactionId ID de la transacción a reembolsar
     * @return PaymentResult con detalles del reembolso
     */
    PaymentResult processRefund(Long paymentTransactionId);

    /**
     * Valida los datos de una tarjeta (sin enviar a externo).
     * @param cardNumber Número de tarjeta
     * @param cvv CVV
     * @param expiryDate Fecha de expiración (MM/YY)
     * @return true si es válida (validación local)
     */
    boolean validateCardData(String cardNumber, String cvv, String expiryDate);

    class PaymentResult {
        public boolean success;
        public String transactionCode; // Código único de transacción
        public String message;
        public String authorizationCode; // Código de autorización simulado
        public java.time.LocalDateTime processedAt;

        public PaymentResult(boolean success, String transactionCode, String message, String authorizationCode) {
            this.success = success;
            this.transactionCode = transactionCode;
            this.message = message;
            this.authorizationCode = authorizationCode;
            this.processedAt = java.time.LocalDateTime.now();
        }
    }
}
