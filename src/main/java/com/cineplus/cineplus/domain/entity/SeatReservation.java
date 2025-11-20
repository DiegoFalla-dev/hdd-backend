package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad para gestionar sesiones de reserva de asientos.
 * Una sesión agrupa múltiples asientos reservados temporalmente por un usuario.
 */
@Entity
@Table(name = "seat_reservations", indexes = {
    @Index(name = "idx_reservation_session", columnList = "sessionId"),
    @Index(name = "idx_reservation_showtime", columnList = "showtime_id"),
    @Index(name = "idx_reservation_expiry", columnList = "expiryTime")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String sessionId; // UUID de la sesión del usuario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Usuario que hizo la reserva (opcional si no está autenticado)

    @Column(nullable = false)
    private LocalDateTime createdAt; // Momento de creación de la reserva

    @Column(nullable = false)
    private LocalDateTime expiryTime; // Momento en que expira (createdAt + 1 minuto)

    @Column(nullable = false)
    private Boolean isActive = true; // Si la reserva está activa

    @Column(nullable = false)
    private Boolean isConfirmed = false; // Si se confirmó la compra

    @Column(length = 50)
    private String purchaseNumber; // Número de orden cuando se confirma

    @ElementCollection
    @CollectionTable(name = "reservation_seat_identifiers", 
                    joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "seat_identifier")
    private Set<String> seatIdentifiers = new HashSet<>(); // IDs de asientos reservados

    /**
     * Verifica si la reserva ha expirado
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime) && !isConfirmed;
    }

    /**
     * Confirma la reserva con un número de compra
     */
    public void confirm(String purchaseNumber) {
        this.isConfirmed = true;
        this.purchaseNumber = purchaseNumber;
        this.isActive = false; // La reserva ya no está activa, se convirtió en compra
    }

    /**
     * Cancela la reserva
     */
    public void cancel() {
        this.isActive = false;
    }
}
