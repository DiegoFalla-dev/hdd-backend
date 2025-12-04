package com.cineplus.cineplus.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderConcessionDTO {
    
    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long productId;
    
    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser positiva")
    private Integer quantity;
    
    @NotNull(message = "El precio unitario no puede ser nulo")
    @Positive(message = "El precio unitario debe ser positivo")
    private BigDecimal unitPrice;
}
