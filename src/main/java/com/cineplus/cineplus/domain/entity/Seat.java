package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats", indexes = {
    @Index(name = "idx_seat_session", columnList = "sessionId"),
    @Index(name = "idx_seat_showtime", columnList = "showtime_id"),
    @Index(name = "idx_seat_coordinates", columnList = "rowPosition, colPosition")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(nullable = false, length = 10)
    private String seatIdentifier; // e.g., "A1", "B10"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Version
    private Long version;

    // Campos para manejo de sesiones y coordenadas
    @Column(length = 100)
    private String sessionId; // ID de sesión del usuario que reservó el asiento

    @Column
    private LocalDateTime reservationTime; // Momento en que se reservó temporalmente

    @Column(length = 50)
    private String purchaseNumber; // Número de orden/compra cuando se confirma

    @Column(nullable = false)
    private Integer rowPosition; // Posición de fila (0-indexed)

    @Column(nullable = false)
    private Integer colPosition; // Posición de columna (0-indexed)

    @Column(nullable = false)
    private Boolean isCancelled = false; // Si está permanentemente cancelado

    public enum SeatStatus {
        AVAILABLE,      // Disponible para reservar
        OCCUPIED,       // Ocupado después de compra confirmada
        TEMPORARILY_RESERVED,  // Reservado temporalmente (1 minuto)
        CANCELLED       // Cancelado permanentemente
    }

    // Constructor para generar asientos iniciales
    public Seat(Long id, Showtime showtime, String seatIdentifier, SeatStatus status, 
                Integer rowPosition, Integer colPosition) {
        this.id = id;
        this.showtime = showtime;
        this.seatIdentifier = seatIdentifier;
        this.status = status;
        this.rowPosition = rowPosition;
        this.colPosition = colPosition;
        this.isCancelled = false;
    }
}
