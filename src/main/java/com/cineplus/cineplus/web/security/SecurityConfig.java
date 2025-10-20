package com.cineplus.cineplus.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF para APIs REST
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").permitAll() // Permitir acceso a todas las APIs por ahora
                        .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
                )
                .httpBasic(withDefaults()); // Opcional: permite autenticación básica para pruebas rápidas

        return http.build();
    }

    // Aquí irían otros beans relacionados con seguridad como PasswordEncoder, UserDetailsService,
    // y los filtros JWT cuando los implementemos.
}