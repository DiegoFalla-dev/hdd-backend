package com.cineplus.cineplus.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "Username or Email cannot be empty")
    private String usernameOrEmail; // Puede ser username o email
    @NotBlank(message = "Password cannot be empty")
    private String password;
}