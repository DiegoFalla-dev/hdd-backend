package com.cineplus.cineplus.domain.entity;

/**
 * Estado de una compra/orden
 */
public enum PurchaseStatus {
    PENDING,        // Pago pendiente de procesamiento
    COMPLETED,      // Pago completado exitosamente
    FAILED,         // Pago fallido
    REFUNDED,       // Compra reembolsada
    CANCELLED       // Compra cancelada
}
