package com.cineplus.cineplus.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.cineplus.cineplus.persistence.service.impl.*;
import com.cineplus.cineplus.web.security.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtils jwtUtils;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                    // Permitir acceso a la autenticación y registro
                    .requestMatchers("/api/auth/**").permitAll()
                    // Permitir GET (lectura) en endpoints públicos sin autenticación
                    .requestMatchers(HttpMethod.GET, "/api/cinemas", "/api/cinemas/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/movies", "/api/movies/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/theaters", "/api/theaters/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/showtimes", "/api/showtimes/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/concessions", "/api/concessions/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/promotions", "/api/promotions/**").permitAll()
                    // Proteger los endpoints de usuario. Solo usuarios autenticados pueden acceder.
                    .requestMatchers("/api/users/**").authenticated()
                    .requestMatchers("/api/orders/**").authenticated()
                    .requestMatchers("/api/payment-methods/**").authenticated()
                    // Cualquier otra solicitud requiere autenticación
                    .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}