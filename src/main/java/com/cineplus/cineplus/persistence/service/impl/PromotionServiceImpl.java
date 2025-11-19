package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.PromotionDTO;
import com.cineplus.cineplus.domain.entity.Promotion;
import com.cineplus.cineplus.domain.repository.PromotionRepository;
import com.cineplus.cineplus.domain.service.PromotionService;
import com.cineplus.cineplus.persistence.mapper.PromotionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Genera un constructor con todos los campos final, inyectando dependencias
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> getAllPromotions() {
        return promotionMapper.toDtoList(promotionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getPromotionById(Long id) {
        return promotionRepository.findById(id).map(promotionMapper::toDto);
    }

    @Override
    @Transactional
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        // Validación de código único antes de guardar
        if (promotionRepository.findByCode(promotionDTO.getCode()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una promoción con el código: " + promotionDTO.getCode());
        }
        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion.setCurrentUses(0); // Asegurarse de que al crear, los usos sean 0
        promotion.setIsActive(true); // Por defecto activa al crear
        return promotionMapper.toDto(promotionRepository.save(promotion));
    }

    @Override
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        return promotionRepository.findById(id).map(existingPromotion -> {
            // Actualizar solo los campos que se permiten modificar
            existingPromotion.setCode(promotionDTO.getCode());
            existingPromotion.setDescription(promotionDTO.getDescription());
            existingPromotion.setDiscountType(promotionDTO.getDiscountType());
            existingPromotion.setValue(promotionDTO.getValue());
            existingPromotion.setStartDate(promotionDTO.getStartDate());
            existingPromotion.setEndDate(promotionDTO.getEndDate());
            existingPromotion.setMinAmount(promotionDTO.getMinAmount());
            existingPromotion.setMaxUses(promotionDTO.getMaxUses());
            existingPromotion.setIsActive(promotionDTO.getIsActive());
            // No se actualiza currentUses directamente desde el DTO para evitar manipulaciones externas
            return promotionMapper.toDto(promotionRepository.save(existingPromotion));
        }).orElseThrow(() -> new RuntimeException("Promoción no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new RuntimeException("Promoción no encontrada con ID: " + id);
        }
        promotionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getActivePromotionByCode(String code) {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByCodeAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(code, now, now)
                .map(promotionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidPromotionForAmount(String promotionCode, BigDecimal totalAmount) {
        if (promotionCode == null || promotionCode.isEmpty() || totalAmount == null) {
            return false;
        }

        Optional<Promotion> promotionOptional = promotionRepository.findByCode(promotionCode);
        if (promotionOptional.isEmpty()) {
            return false; // Promoción no encontrada
        }

        Promotion promotion = promotionOptional.get();
        LocalDateTime now = LocalDateTime.now();

        // Verificar si la promoción está activa por fecha y estado
        if (!promotion.getIsActive() || now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            return false; // Promoción inactiva o fuera de rango de fechas
        }

        // Verificar usos máximos
        if (promotion.getMaxUses() != null && promotion.getCurrentUses() >= promotion.getMaxUses()) {
            return false; // Promoción ha alcanzado su límite de usos
        }

        // Verificar monto mínimo
        if (promotion.getMinAmount() != null && totalAmount.compareTo(promotion.getMinAmount()) < 0) {
            return false; // El monto de la compra es menor al mínimo requerido para la promoción
        }

        return true; // La promoción es válida y aplicable
    }
}