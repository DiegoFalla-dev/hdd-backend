package com.cineplus.cineplus.domain.service;

/**
 * Servicio de notificaciones por SMS SIMULADO (sin API SMS externa).
 * Almacena los SMS en logs y BD simulada.
 */
public interface SmsNotificationService {

    /**
     * Envía un SMS de confirmación de orden.
     * @param phoneNumber Número de teléfono (formato: 9XXXXXXXXX para Perú)
     * @param orderId ID de la orden
     * @param totalAmount Monto total de la compra
     * @return SmsResult con detalles del envío
     */
    SmsResult sendOrderConfirmationSms(String phoneNumber, Long orderId, java.math.BigDecimal totalAmount);

    /**
     * Envía un SMS de recordatorio de película.
     * @param phoneNumber Número de teléfono
     * @param movieTitle Título de la película
     * @param showtimeDate Fecha y hora de la función
     * @return SmsResult con detalles del envío
     */
    SmsResult sendMovieReminderSms(String phoneNumber, String movieTitle, String showtimeDate);

    /**
     * Valida el formato de un número de teléfono peruano.
     * @param phoneNumber Teléfono a validar
     * @return true si es válido (comienza con 9 y tiene 9 dígitos)
     */
    boolean isValidPhoneNumber(String phoneNumber);

    class SmsResult {
        public boolean success;
        public String smsId;            // ID único del SMS
        public String phoneNumber;
        public String message;
        public String status;           // SENT, PENDING, FAILED
        public java.time.LocalDateTime sentAt;

        public SmsResult(boolean success, String smsId, String phoneNumber, String message, String status) {
            this.success = success;
            this.smsId = smsId;
            this.phoneNumber = phoneNumber;
            this.message = message;
            this.status = status;
            this.sentAt = java.time.LocalDateTime.now();
        }
    }
}
