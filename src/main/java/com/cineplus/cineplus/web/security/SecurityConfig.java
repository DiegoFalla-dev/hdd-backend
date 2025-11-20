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
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

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
                        .requestMatchers(antMatcher("/api/auth/**")).permitAll()
                        // Permitir GET (lectura) en endpoints públicos sin autenticación
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/cinemas/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/movies/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/theaters/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/showtimes/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/concessions/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/promotions/**")).permitAll()
                        // Proteger los endpoints de usuario. Solo usuarios autenticados pueden acceder.
                        .requestMatchers(antMatcher("/api/users/**")).authenticated()
                        .requestMatchers(antMatcher("/api/orders/**")).authenticated()
                        .requestMatchers(antMatcher("/api/payment-methods/**")).authenticated()
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}