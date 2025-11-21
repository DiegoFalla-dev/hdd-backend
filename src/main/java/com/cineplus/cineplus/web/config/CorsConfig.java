package com.cineplus.cineplus.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Arrays;

@Configuration
public class CorsConfig {
    @Value("${FRONTEND_ORIGIN:http://localhost:5173}")
    private String frontendOrigin;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allow multiple origins separated by comma in FRONTEND_ORIGIN
                String[] origins = Arrays.stream(frontendOrigin.split(","))
                        .map(String::trim)
                        .toArray(String[]::new);

                registry.addMapping("/**") // Aplica a todos los endpoints
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}