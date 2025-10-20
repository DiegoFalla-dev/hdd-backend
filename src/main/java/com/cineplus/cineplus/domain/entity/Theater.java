package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "theaters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false, length = 100)
    private String name; // e.g., "Sala 1", "Sala XD"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatMatrixType seatMatrixType; // SMALL, MEDIUM, LARGE, XLARGE

    private int rows;
    private int cols;
    private int totalSeats;

    public enum SeatMatrixType {
        SMALL, MEDIUM, LARGE, XLARGE
    }
}