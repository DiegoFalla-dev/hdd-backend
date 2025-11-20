package com.cineplus.cineplus.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodCreateDto {
    private String cardNumber;
    private String cardHolder;
    private String cci;
    private String expiry; // MM/YY or ISO
    private String phone;
    private Boolean isDefault;
}
