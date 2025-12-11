package com.cineplus.cineplus.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String nationalId;
    private String email;
    private String birthDate;
    private String avatar;
    private Set<String> roles;
    private List<PaymentMethodDto> paymentMethods;
    
    // Información de fidelización
    private Integer fidelityPoints = 0;
    private LocalDateTime lastPurchaseDate;
}