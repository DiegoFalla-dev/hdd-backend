package com.cineplus.cineplus.domain.service.impl;

import com.cineplus.cineplus.domain.service.SmsNotificationService;
import org.springframework.stereotype.Service;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsNotificationServiceImpl implements SmsNotificationService {

    private static final String SMS_LOG_FILE = "/tmp/cineplus_sms_log.txt";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Map<String, SmsResult> smsHistory = new ConcurrentHashMap<>();
    private long smsCounter = 0;

    @Override
    public SmsResult sendOrderConfirmationSms(String phoneNumber, Long orderId, BigDecimal totalAmount) {
        if (!isValidPhoneNumber(phoneNumber)) {
            return new SmsResult(false, null, phoneNumber, "Número de teléfono inválido", "FAILED");
        }

        String smsId = generateSmsId();
        String message = String.format(
            "CINEPLUS: Tu orden #%d por S/. %.2f ha sido confirmada. " +
            "Espera la confirmación de pago. ¡Disfruta tu película!",
            orderId, totalAmount
        );

        SmsResult result = new SmsResult(true, smsId, phoneNumber, message, "SENT");
        logSms(result, "ORDER_CONFIRMATION");
        smsHistory.put(smsId, result);

        System.out.println("[SMS] ✓ SMS de confirmación enviado a " + phoneNumber);
        System.out.println("  ID: " + smsId);
        System.out.println("  Mensaje: " + message);

        return result;
    }

    @Override
    public SmsResult sendMovieReminderSms(String phoneNumber, String movieTitle, String showtimeDate) {
        if (!isValidPhoneNumber(phoneNumber)) {
            return new SmsResult(false, null, phoneNumber, "Número de teléfono inválido", "FAILED");
        }

        String smsId = generateSmsId();
        String message = String.format(
            "CINEPLUS: Recordatorio: '%s' comienza el %s. " +
            "¡No olvides tus entradas!",
            movieTitle, showtimeDate
        );

        SmsResult result = new SmsResult(true, smsId, phoneNumber, message, "SENT");
        logSms(result, "REMINDER");
        smsHistory.put(smsId, result);

        System.out.println("[SMS] ✓ SMS de recordatorio enviado a " + phoneNumber);
        System.out.println("  ID: " + smsId);
        System.out.println("  Película: " + movieTitle);

        return result;
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        // Formato Perú: 9 al inicio + 8 dígitos = 9 dígitos totales
        return phoneNumber.matches("^9\\d{8}$");
    }

    /**
     * Genera un ID único para cada SMS.
     */
    private String generateSmsId() {
        return String.format("SMS-%d-%d", ++smsCounter, System.currentTimeMillis());
    }

    /**
     * Registra el SMS en archivo de log (simulación de persistencia).
     */
    private void logSms(SmsResult result, String type) {
        try {
            String logEntry = String.format(
                "[%s] [%s] %s | Phone: %s | Message: %s | Status: %s\n",
                result.sentAt.format(DATE_FORMAT),
                type,
                result.smsId,
                result.phoneNumber,
                result.message,
                result.status
            );

            synchronized (this) {
                try (FileWriter fw = new FileWriter(SMS_LOG_FILE, true);
                     BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write(logEntry);
                }
            }
        } catch (IOException e) {
            System.err.println("[SMS-ERROR] No se pudo escribir el log: " + e.getMessage());
        }
    }

    /**
     * Obtiene el historial de SMS enviados (útil para testing).
     */
    public Map<String, SmsResult> getSmsHistory() {
        return new HashMap<>(smsHistory);
    }

    /**
     * Limpiar historial (útil para testing).
     */
    public void clearHistory() {
        smsHistory.clear();
        smsCounter = 0;
    }
}
