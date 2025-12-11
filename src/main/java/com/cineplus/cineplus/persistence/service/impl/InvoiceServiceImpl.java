package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.repository.OrderRepository;
import com.cineplus.cineplus.domain.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private final OrderRepository orderRepository;
    
    // Empresa simulada (CINEPLUS S.A.)
    private static final String COMPANY_RUC = "20123456789";
    private static final String COMPANY_NAME = "CINEPLUS S.A.";

    @Override
    public InvoiceResult generateInvoice(Long orderId, String ruc, String serieNumber) {
        try {
            // Validar RUC
            if (!isValidRuc(ruc)) {
                log.warn("[INVOICE] RUC inválido: {}", ruc);
                return new InvoiceResult(
                    false, null, ruc, null, null, null, null,
                    "RUC inválido. Debe tener 11 dígitos."
                );
            }

            // Generar número de boleta
            String invoiceNumber = generateInvoiceNumber(serieNumber);
            LocalDateTime now = LocalDateTime.now();
            String issueDateStr = now.format(DateTimeFormatter.ISO_DATE_TIME);

            // Generar datos QR simulado
            String qrData = generateQRData(invoiceNumber, ruc, COMPANY_NAME, orderId, issueDateStr);

            // URL simulada de descarga
            String invoiceUrl = "http://localhost:8080/api/invoices/" + invoiceNumber + "/download";

            log.info("[INVOICE] ✓ Boleta generada | Número: {} | RUC: {} | QR: {}", 
                    invoiceNumber, ruc, qrData.substring(0, Math.min(50, qrData.length())) + "...");

            return new InvoiceResult(
                true,
                invoiceNumber,
                ruc,
                COMPANY_NAME,
                issueDateStr,
                invoiceUrl,
                qrData,
                "Boleta generada exitosamente"
            );

        } catch (Exception e) {
            log.error("[INVOICE] Error generando boleta: {}", e.getMessage());
            return new InvoiceResult(
                false, null, ruc, null, null, null, null,
                "Error generando boleta: " + e.getMessage()
            );
        }
    }

    @Override
    public boolean isValidRuc(String ruc) {
        if (ruc == null) return false;

        // RUC debe tener exactamente 11 dígitos
        if (!Pattern.matches("^\\d{11}$", ruc)) {
            return false;
        }

        // Validación de RUC peruano (dígito verificador)
        return validateRucChecksum(ruc);
    }

    private String generateInvoiceNumber(String serieNumber) {
        String series = (serieNumber != null && !serieNumber.isEmpty()) ? serieNumber : "B001";
        
        // Leer el último número de factura de la BD
        long lastSequence = getLastInvoiceSequence(series);
        long nextSequence = lastSequence + 1;
        
        // Formato: B001-000001
        return String.format("%s-%06d", series, nextSequence);
    }

    /**
     * Obtiene el último número de secuencia de factura de la serie especificada
     * leyendo de la base de datos.
     */
    private long getLastInvoiceSequence(String series) {
        try {
            // Buscar la última orden con un invoiceNumber que comience con la serie
            String prefix = series + "-";
            
            // Obtenemos todas las órdenes y buscamos la que tiene el invoiceNumber más alto
            // En producción, usaríamos una query SQL más eficiente
            long maxSequence = 0;
            
            // Query: SELECT MAX(CAST(SUBSTRING(invoice_number, LENGTH(series)+2) AS UNSIGNED)) 
            // FROM orders WHERE invoice_number LIKE 'B001-%'
            // Por simplicidad, iteramos (esto se puede optimizar)
            long[] maxSeq = {maxSequence};
            orderRepository.findAll().forEach(order -> {
                if (order.getInvoiceNumber() != null && order.getInvoiceNumber().startsWith(prefix)) {
                    try {
                        String numPart = order.getInvoiceNumber().substring(prefix.length());
                        long seq = Long.parseLong(numPart);
                        if (seq > maxSeq[0]) {
                            maxSeq[0] = seq;
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar si no puede parsear
                    }
                }
            });
            maxSequence = maxSeq[0];
            
            return maxSequence;
        } catch (Exception e) {
            log.warn("[INVOICE] Error obteniendo última secuencia de factura: {}", e.getMessage());
            return 0; // Empezar desde 1 si hay error
        }
    }

    private String generateQRData(String invoiceNumber, String ruc, String companyName, 
                                  Long orderId, String issueDate) {
        // Formato simplificado de QR para boleta electrónica
        // Estructura: RUC|companyName|invoiceNumber|issueDate|orderId|CINEPLUS
        return String.format(
            "%s|%s|%s|%s|%d|SIMULADO",
            ruc, companyName, invoiceNumber, issueDate, orderId
        );
    }

    /**
     * Validación del dígito verificador de RUC peruano (simplificada).
     * Nota: Esta es una validación simbólica, no la validación real del SUNAT.
     */
    private boolean validateRucChecksum(String ruc) {
        if (ruc.length() != 11) return false;

        try {
            // Para este simulador, aceptamos cualquier RUC con 11 dígitos válidos
            // En producción, se usaría la fórmula real del dígito verificador
            long num = Long.parseLong(ruc);
            
            // Validación simbólica: el RUC no puede ser 0
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
