package com.cineplus.cineplus.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Mantén este si lo usas en otros lugares

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Este genera el constructor de 6 argumentos
public class JwtResponseDto {
    private String token;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String type = "Bearer";
    private String favoriteCinema;
    
    // Información de fidelización
    private Integer fidelityPoints;
    private LocalDateTime lastPurchaseDate;

    // Constructor personalizado que usa el valor por defecto de 'type'
    public JwtResponseDto(String token, Long id, String username, String email, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.type = "Bearer"; // Se inicializa aquí
    }

    public JwtResponseDto(String token, Long id, String username, String email, List<String> roles, String favoriteCinema) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.type = "Bearer";
        this.favoriteCinema = favoriteCinema;
    }
    
    // Constructor con fidelización
    public JwtResponseDto(String token, Long id, String username, String email, List<String> roles, 
                         String favoriteCinema, Integer fidelityPoints, LocalDateTime lastPurchaseDate) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.type = "Bearer";
        this.favoriteCinema = favoriteCinema;
        this.fidelityPoints = fidelityPoints;
        this.lastPurchaseDate = lastPurchaseDate;
    }
}