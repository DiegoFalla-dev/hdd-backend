package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(nullable = false, length = 100)
    private String genre;

    @Column(nullable = false, length = 50)
    private String classification; // G, PG, PG-13, R, NC-17

    @Column(nullable = false, length = 20)
    private String duration; // e.g., "1h 45m"

    private String cardImageUrl; // URL de la imagen para la tarjeta
    private String bannerUrl;    // URL de la imagen del banner
}