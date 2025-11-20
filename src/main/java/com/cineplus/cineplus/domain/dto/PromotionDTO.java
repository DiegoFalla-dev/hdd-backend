package com.cineplus.cineplus.domain.dto;

import com.cineplus.cineplus.domain.entity.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionDTO {
    private Long id;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal value;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minAmount;
    private Integer maxUses;
    private Integer currentUses; // Puede ser útil para mostrar cuántas veces se ha usado
    private Boolean isActive;
}