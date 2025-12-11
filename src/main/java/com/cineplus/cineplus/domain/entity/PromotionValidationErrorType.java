package com.cineplus.cineplus.domain.entity;

/**
 * Enum que especifica los diferentes tipos de errores de validación de promociones
 */
public enum PromotionValidationErrorType {
    // Promoción válida
    VALID("La promoción es válida"),
    
    // Promoción de un solo uso ya fue consumida
    SINGLE_USE_EXPIRED("Este código promocional ya fue usado"),
    
    // Promoción de múltiples usos agotó sus usos
    USAGE_LIMIT_EXCEEDED("Este código promocional agotó sus usos"),
    
    // Rango de fechas de vigencia expirado
    DATE_RANGE_EXPIRED("Este código promocional ha expirado"),
    
    // Monto de compra menor al mínimo requerido
    MIN_AMOUNT_NOT_MET("El monto de compra es menor al mínimo requerido para esta promoción"),
    
    // Promoción no encontrada
    PROMOTION_NOT_FOUND("El código de promoción no existe"),
    
    // Promoción inactiva manualmente
    PROMOTION_INACTIVE("Este código promocional no está activo"),
    
    // Error genérico
    GENERIC("Promoción no válida o no aplicable para este monto");
    
    private final String message;
    
    PromotionValidationErrorType(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
