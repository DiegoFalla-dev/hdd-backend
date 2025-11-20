package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para un ítem de compra
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDto {
    private Long id;
    private String itemType;       // "TICKET" o "CONCESSION"
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private Long concessionProductId;
    private String seatIdentifiers; // "A1,A2,A3" para entradas
}
