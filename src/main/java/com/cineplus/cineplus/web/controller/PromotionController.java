package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.PromotionDTO;
import com.cineplus.cineplus.domain.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
        List<PromotionDTO> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Long id) {
        return promotionService.getPromotionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PromotionDTO> getActivePromotionByCode(@PathVariable String code) {
        return promotionService.getActivePromotionByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para validar una promoción contra un monto total.
     * Responde con un objeto que incluye:
     * - isValid: booleano indicando si la promoción es aplicable
     * - errorType: tipo de error específico si no es válida
     * - promotion: los detalles de la promoción (si es válida)
     * - message: mensaje explicativo
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePromotion(
            @RequestParam String code,
            @RequestParam BigDecimal amount) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (code == null || code.isEmpty()) {
            response.put("isValid", false);
            response.put("errorType", "PROMOTION_NOT_FOUND");
            response.put("message", "Código de promoción requerido");
            return ResponseEntity.badRequest().body(response);
        }

        // Obtener el tipo de error específico de validación
        com.cineplus.cineplus.domain.entity.PromotionValidationErrorType errorType = promotionService.validatePromotionWithErrorType(code, amount);
        
        response.put("isValid", errorType == com.cineplus.cineplus.domain.entity.PromotionValidationErrorType.VALID);
        response.put("errorType", errorType.name());
        response.put("message", errorType.getMessage());
        
        if (errorType == com.cineplus.cineplus.domain.entity.PromotionValidationErrorType.VALID) {
            // Si es válida, retornar también los detalles de la promoción
            promotionService.getActivePromotionByCode(code).ifPresent(promo -> {
                response.put("promotion", promo);
            });
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden crear promociones
    public ResponseEntity<PromotionDTO> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO) {
        PromotionDTO createdPromotion = promotionService.createPromotion(promotionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden actualizar promociones
    public ResponseEntity<PromotionDTO> updatePromotion(@PathVariable Long id, @Valid @RequestBody PromotionDTO promotionDTO) {
        try {
            PromotionDTO updatedPromotion = promotionService.updatePromotion(id, promotionDTO);
            return ResponseEntity.ok(updatedPromotion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden eliminar promociones
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        try {
            promotionService.deletePromotion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}