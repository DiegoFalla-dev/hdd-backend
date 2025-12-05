package com.cineplus.cineplus.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    private String nationalId; // DNI

    @NotBlank @Email(message = "Email should be valid")
    private String email;

    private String birthDate;
    private String gender;
    private String phone;

    @NotBlank(message = "Password must not be blank")
    private String password;
    @NotBlank(message = "Confirm password must not be blank")
    private String confirmPassword;

    private String avatar;
    private Set<String> roles;
    private String favoriteCinema;
}