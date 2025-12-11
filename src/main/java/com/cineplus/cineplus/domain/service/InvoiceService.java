package com.cineplus.cineplus.domain.service;

import java.time.LocalDateTime;

/**
 * Servicio para generar boletas electrónicas SIMULADAS (sin integración SUNAT real).
 * Genera comprobantes con formato de boleta como si fueran validadas.
 */
public interface InvoiceService {

    /**
     * Genera una boleta electrónica simulada.
     * @param orderId ID de la orden
     * @param ruc RUC de la empresa (ej: 20123456789)
     * @param serieNumber Número de serie (ej: B001)
     * @return InvoiceResult con datos de la boleta
     */
    InvoiceResult generateInvoice(Long orderId, String ruc, String serieNumber);

    /**
     * Valida el formato de un RUC.
     * @param ruc RUC a validar
     * @return true si el formato es válido
     */
    boolean isValidRuc(String ruc);

    class InvoiceResult {
        public boolean success;
        public String invoiceNumber;        // Ej: B001-000001
        public String ruc;
        public String companyName;          // Ej: CINEPLUS S.A.
        public String issueDate;            // ISO format
        public String invoiceUrl;           // URL simulada para descargar
        public String qrCode;               // Data del QR
        public String message;

        public InvoiceResult(boolean success, String invoiceNumber, String ruc, String companyName,
                            String issueDate, String invoiceUrl, String qrCode, String message) {
            this.success = success;
            this.invoiceNumber = invoiceNumber;
            this.ruc = ruc;
            this.companyName = companyName;
            this.issueDate = issueDate;
            this.invoiceUrl = invoiceUrl;
            this.qrCode = qrCode;
            this.message = message;
        }
    }
}
