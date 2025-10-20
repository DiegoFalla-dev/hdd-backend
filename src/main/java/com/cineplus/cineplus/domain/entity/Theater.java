package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set; // Importa Set si Cinema tiene una lista de Theaters

@Entity
@Table(name = "theaters") // <-- Asegúrate de que esta anotación esté presente y sea correcta
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false) // <-- Clave foránea a la tabla 'cinemas'
    private Cinema cinema;

    @Column(nullable = false, length = 100)
    private String name; // e.g., "Sala 1", "Sala XD"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatMatrixType seatMatrixType; // SMALL, MEDIUM, LARGE, XLARGE

    private int rows;
    private int cols;
    private int totalSeats;

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Showtime> showtimes; // Relación con Showtime (opcional, pero buena práctica si quieres acceder a los showtimes desde la sala)

    public enum SeatMatrixType {
        SMALL, MEDIUM, LARGE, XLARGE
    }
}