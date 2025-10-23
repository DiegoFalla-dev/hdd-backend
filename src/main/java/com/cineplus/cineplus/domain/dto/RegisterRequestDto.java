package com.cineplus.cineplus.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    private String firstName;
    private String lastName;
    private String username; // optional
    private String nationalId; // DNI
    private String email;
    private String birthDate;
    private String phone; // celular (plain text in DTO, will be encrypted before saving)
    private String password;
    private String confirmPassword;
    private String avatar;
    private Set<String> roles; // role names (e.g. "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER")
}