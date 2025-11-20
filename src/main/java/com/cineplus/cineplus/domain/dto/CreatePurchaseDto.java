package com.cineplus.cineplus.domain.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear una nueva compra
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePurchaseDto {
    
    /**
     * ID de sesión de reserva temporal
     */
    @NotBlank(message = "SessionId is required")
    private String sessionId;
    
    /**
     * ID del usuario que realiza la compra
     */
    @NotNull(message = "UserId is required")
    private Long userId;
    
    /**
     * ID del método de pago a utilizar
     */
    @NotNull(message = "PaymentMethodId is required")
    private Long paymentMethodId;
    
    /**
     * Monto total a cobrar (debe coincidir con el cálculo backend)
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    /**
     * Items que se están comprando
     */
    @NotEmpty(message = "At least one item is required")
    private List<PurchaseItemRequestDto> items;
}
