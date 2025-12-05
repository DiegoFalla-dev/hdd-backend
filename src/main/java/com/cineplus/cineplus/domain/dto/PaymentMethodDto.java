package com.cineplus.cineplus.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDto {
    private Long id;
    private String type; // CARD or YAPE
    private Boolean isDefault;
    // No raw sensitive data exposed. Optionally show masked card number
    private String maskedCardNumber;
    private String brand; // Para tarjetas (Visa, Mastercard, etc)
    private String last4; // Últimos 4 dígitos
    private String holderName; // Para YAPE, nombre del titular
    private String phone; // Para YAPE, número de teléfono
    private Integer expMonth; // Para tarjetas
    private Integer expYear; // Para tarjetas
}
