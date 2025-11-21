package com.cineplus.cineplus.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.filter.CorsFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class CorsConfig {
    @Value("${FRONTEND_ORIGIN:http://localhost:5173}")
    private String frontendOrigin;

    // Temporary emergency flag to allow all origins (for quick testing). Set to true only for debugging.
    @Value("${ALLOW_ALL_CORS:false}")
    private boolean allowAllCors;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allow multiple origins separated by comma in FRONTEND_ORIGIN
            // If emergency flag is set, allow all origins (use origin patterns to work with credentials)
            if (allowAllCors || "*".equals(frontendOrigin.trim())) {
                registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            } else {
                String[] origins = Arrays.stream(frontendOrigin.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);

                registry.addMapping("/**") // Aplica a todos los endpoints
                    .allowedOrigins(origins)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
            }
        };
    }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(frontendOrigin.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();

        if (allowAllCors || "*".equals(frontendOrigin.trim())) {
            configuration.setAllowedOriginPatterns(List.of("*"));
        } else {
            configuration.setAllowedOrigins(origins);
        }
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
        }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilterBean(CorsConfigurationSource source) {
        // Ensure CORS headers are added as early as possible
        return new CorsFilter(source);
    }
}