package com.cineplus.cineplus.web.config;

public final class SecurityConstants {
    private SecurityConstants() {}

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String[] PUBLIC_ENDPOINTS = new String[] {
        "/api/auth/**",
        "/api/public/**",
        "/v3/api-docs/**",
        "/swagger-ui/**"
    };
}
