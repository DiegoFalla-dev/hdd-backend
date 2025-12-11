package com.cineplus.cineplus.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * UTILIDAD CENTRALIZADA DE CÁLCULO DE PRECIOS
 * 
 * Esta utilidad se usa en todo el sistema para garantizar consistencia
 * en el cálculo de subtotales, descuentos e IGV (18%)
 * 
 * Flujo:
 * 1. Subtotal = suma de precios de entradas + productos
 * 2. Descuento = aplicar promoción al subtotal (si aplica)
 * 3. Base imponible = Subtotal - Descuento
 * 4. IGV (18%) = Base imponible * 0.18
 * 5. Total = Base imponible + IGV
 */
public class PriceCalculationUtil {
    
    public static final BigDecimal IGV_RATE = new BigDecimal("0.18");
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    /**
     * DTO que contiene el desglose completo de precios
     */
    public static class PriceBreakdown {
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private BigDecimal subtotalAfterDiscount;
        private BigDecimal taxAmount;
        private BigDecimal grandTotal;
        
        public PriceBreakdown(BigDecimal subtotal, BigDecimal discountAmount, 
                            BigDecimal subtotalAfterDiscount, BigDecimal taxAmount, BigDecimal grandTotal) {
            this.subtotal = subtotal;
            this.discountAmount = discountAmount;
            this.subtotalAfterDiscount = subtotalAfterDiscount;
            this.taxAmount = taxAmount;
            this.grandTotal = grandTotal;
        }
        
        // Getters
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public BigDecimal getSubtotalAfterDiscount() { return subtotalAfterDiscount; }
        public BigDecimal getTaxAmount() { return taxAmount; }
        public BigDecimal getGrandTotal() { return grandTotal; }
    }
    
    /**
     * Calcula el desglose de precios completo
     * @param subtotal Suma de todos los precios de artículos
     * @param discountAmount Monto del descuento (puede ser 0)
     * @return PriceBreakdown con todos los cálculos
     */
    public static PriceBreakdown calculatePriceBreakdown(BigDecimal subtotal, BigDecimal discountAmount) {
        // Asegurar valores numéricos válidos
        BigDecimal cleanSubtotal = subtotal == null ? BigDecimal.ZERO : subtotal;
        if (cleanSubtotal.signum() < 0) cleanSubtotal = BigDecimal.ZERO;
        
        BigDecimal cleanDiscount = discountAmount == null ? BigDecimal.ZERO : discountAmount;
        if (cleanDiscount.signum() < 0) cleanDiscount = BigDecimal.ZERO;
        
        // Base imponible: subtotal menos descuentos
        BigDecimal subtotalAfterDiscount = cleanSubtotal.subtract(cleanDiscount);
        if (subtotalAfterDiscount.signum() < 0) subtotalAfterDiscount = BigDecimal.ZERO;
        
        // IGV: 18% sobre la base imponible
        BigDecimal taxAmount = subtotalAfterDiscount
            .multiply(IGV_RATE)
            .setScale(SCALE, ROUNDING_MODE);
        
        // Total: base imponible + IGV
        BigDecimal grandTotal = subtotalAfterDiscount
            .add(taxAmount)
            .setScale(SCALE, ROUNDING_MODE);
        
        // Redondear valores
        cleanSubtotal = cleanSubtotal.setScale(SCALE, ROUNDING_MODE);
        cleanDiscount = cleanDiscount.setScale(SCALE, ROUNDING_MODE);
        subtotalAfterDiscount = subtotalAfterDiscount.setScale(SCALE, ROUNDING_MODE);
        
        return new PriceBreakdown(cleanSubtotal, cleanDiscount, subtotalAfterDiscount, taxAmount, grandTotal);
    }
    
    /**
     * Calcula solo el monto de IGV
     */
    public static BigDecimal calculateTax(BigDecimal base) {
        if (base == null) base = BigDecimal.ZERO;
        if (base.signum() < 0) base = BigDecimal.ZERO;
        
        return base
            .multiply(IGV_RATE)
            .setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Calcula el total (base + IGV)
     */
    public static BigDecimal calculateGrandTotal(BigDecimal subtotal, BigDecimal discountAmount) {
        PriceBreakdown breakdown = calculatePriceBreakdown(subtotal, discountAmount);
        return breakdown.getGrandTotal();
    }
    
    /**
     * Valida que el total calculado sea correcto
     */
    public static boolean validateTotalCalculation(BigDecimal subtotal, BigDecimal discountAmount, 
                                                   BigDecimal expectedGrandTotal) {
        BigDecimal calculatedTotal = calculateGrandTotal(subtotal, discountAmount);
        // Permitir diferencias de hasta 0.01 (redondeo)
        return Math.abs(calculatedTotal.subtract(expectedGrandTotal).doubleValue()) <= 0.01;
    }
}
