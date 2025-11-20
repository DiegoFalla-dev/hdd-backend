package com.cineplus.cineplus.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para un ítem dentro de una solicitud de compra
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemRequestDto {
    
    @NotBlank(message = "Item type is required")
    private String type;            // "TICKET" o "CONCESSION"
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    // Opcional: ID del producto de confitería
    private Long concessionProductId;
    
    // Opcional: Identificadores de asientos para entradas
    private String seatIdentifiers;
}
