package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.CreatePurchaseDto;
import com.cineplus.cineplus.domain.dto.PaymentResponseDto;
import com.cineplus.cineplus.domain.dto.PurchaseDto;
import com.cineplus.cineplus.domain.entity.Purchase;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar compras y pagos
 */
public interface PurchaseService {

    /**
     * Procesa una compra completa:
     * 1. Valida la sesión de reserva
     * 2. Valida el monto total
     * 3. Procesa el pago con el método seleccionado
     * 4. Crea el registro de compra
     * 5. Confirma los asientos como OCCUPIED
     * 6. Retorna el purchaseNumber generado
     */
    PaymentResponseDto processPurchase(CreatePurchaseDto request);

    /**
     * Obtiene el historial de compras de un usuario
     */
    List<PurchaseDto> getUserPurchases(Long userId);

    /**
     * Obtiene el detalle completo de una compra por su número
     */
    Optional<PurchaseDto> getPurchaseByNumber(String purchaseNumber);

    /**
     * Genera un purchaseNumber único
     * Formato: CIN-yyyyMMddHHmmss-XXXXXXXX
     */
    String generatePurchaseNumber();
}
