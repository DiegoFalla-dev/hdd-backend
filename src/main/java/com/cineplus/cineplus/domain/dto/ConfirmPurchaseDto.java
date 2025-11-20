package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para confirmar una compra de asientos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPurchaseDto {
    private String sessionId;
    private String purchaseNumber;
}
