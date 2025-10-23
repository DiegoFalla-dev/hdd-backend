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
    private Boolean isDefault;
    // No raw sensitive data exposed. Optionally show masked card number
    private String maskedCardNumber;
}
