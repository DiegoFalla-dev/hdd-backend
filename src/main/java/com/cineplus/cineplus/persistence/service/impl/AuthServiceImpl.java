package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.JwtResponseDto;
import com.cineplus.cineplus.domain.dto.LoginRequestDto;
import com.cineplus.cineplus.domain.dto.RegisterRequestDto;
import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.service.AuthService;
import com.cineplus.cineplus.domain.service.UserService;
import com.cineplus.cineplus.web.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public JwtResponseDto registerUser(RegisterRequestDto registerRequest) {
        User registeredUser = userService.registerNewUser(registerRequest);

        // Una vez registrado, autentica al usuario para generar un token directamente
        // Aunque generalmente se pide al usuario que haga login después de registrarse.
        // Para simplificar, generamos el token aquí.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return new JwtResponseDto(
                jwt,
                "Bearer",
                registeredUser.getId(),
                userDetails.getUsername(),
                registeredUser.getEmail(),
                userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.joining(",")));
    }


    @Override
    @Transactional(readOnly = true)
    public JwtResponseDto authenticateUser(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername()) // Recuperar el User entity completo para el ID y email
                .orElse(null); // Esto no debería pasar si la autenticación fue exitosa

        return new JwtResponseDto(
                jwt,
                "Bearer",
                user != null ? user.getId() : null, // ID del usuario
                userDetails.getUsername(),
                user != null ? user.getEmail() : null, // Email del usuario
                userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.joining(",")));
    }
}