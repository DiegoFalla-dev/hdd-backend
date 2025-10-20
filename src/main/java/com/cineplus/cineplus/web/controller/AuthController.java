package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.JwtResponseDto;
import com.cineplus.cineplus.domain.dto.LoginRequestDto;
import com.cineplus.cineplus.domain.dto.RegisterRequestDto;
import com.cineplus.cineplus.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto registerRequest) {
        JwtResponseDto response = authService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        JwtResponseDto response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }
}