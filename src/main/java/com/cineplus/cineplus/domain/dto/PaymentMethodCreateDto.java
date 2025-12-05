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
    private String type; // "CARD" or "YAPE"
    private String cardNumber; // para CARD
    private String cardHolder; // para CARD
    private String cci; // para CARD (CVC)
    private String expiry; // MM/YY or ISO - para CARD
    private String phone; // para YAPE
    private String verificationCode; // para YAPE
    private Boolean isDefault;
}
