package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta después de procesar un pago
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private Boolean success;
    private String purchaseNumber;
    private String transactionId;
    private String message;
}
