package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa el precio/linea de entradas asociado a una orden.
 * Permite almacenar el precio unitario, la cantidad y el tipo de entrada
 * para mantener un historial y reconstituir totales por orden.
 */
@Entity
@Table(name = "ticket_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "showtime_id")
    private Long showtimeId; // referencia a la función asociada (opcional)

    @Column(name = "ticket_type", length = 100)
    private String ticketType; // Ej: ADULT, CHILD, SENIOR, VIP, GENERAL

    @Column(name = "seat_code", length = 32)
    private String seatCode; // opcional, si corresponde a asiento concreto

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
