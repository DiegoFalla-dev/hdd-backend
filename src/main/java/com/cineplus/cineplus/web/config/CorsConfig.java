package com.cineplus.cineplus.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS para el backend de CinePlus
 * 
 * IMPORTANTE: URLs permitidas para solicitudes desde el frontend
 * - http://localhost:5173 (Vite dev server - puerto principal)
 * - http://localhost:5174 (Vite dev server - puerto alternativo) ⚠️ FALTA AGREGAR
 * 
 * Si el frontend cambia de puerto o se despliega en producción:
 * 1. Actualizar .allowedOrigins() en este archivo
 * 2. Actualizar application.properties (spring.web.cors.allowed-origins)
 * 3. Actualizar @CrossOrigin en todos los controladores
 * 4. Verificar SecurityConfig.java
 * 
 * NOTA: Este archivo configura CORS a nivel de aplicación web (Spring MVC)
 * SecurityConfig.java configura CORS a nivel de seguridad (Spring Security)
 * Ambos deben tener las mismas URLs permitidas para evitar conflictos
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Aplica a todos los endpoints
                        .allowedOrigins("http://localhost:5173", "http://localhost:5174") // Frontend Vite (ambos puertos)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}