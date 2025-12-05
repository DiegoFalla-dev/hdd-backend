package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.PromotionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionService {

    List<PromotionDTO> getAllPromotions();
    Optional<PromotionDTO> getPromotionById(Long id);
    PromotionDTO createPromotion(PromotionDTO promotionDTO);
    PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO);
    void deletePromotion(Long id);

    /**
     * Busca una promoción activa por su código.
     * @param code El código de la promoción.
     * @return Un Optional que contiene la PromotionDTO si se encuentra y está activa, de lo contrario, vacío.
     */
    Optional<PromotionDTO> getActivePromotionByCode(String code);

    /**
     * Valida si una promoción es aplicable a un monto total.
     * @param promotionCode El código de la promoción.
     * @param totalAmount El monto total de la compra.
     * @return true si la promoción es aplicable, false en caso contrario.
     */
    boolean isValidPromotionForAmount(String promotionCode, BigDecimal totalAmount); // Necesita BigDecimal
}