package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.JwtResponseDto;
import com.cineplus.cineplus.domain.dto.LoginRequestDto;
import com.cineplus.cineplus.domain.dto.RegisterRequestDto;

public interface AuthService {
    JwtResponseDto authenticateUser(LoginRequestDto loginRequest);
    void registerUser(RegisterRequestDto registerRequest);
    JwtResponseDto refreshToken(String refreshToken);
}