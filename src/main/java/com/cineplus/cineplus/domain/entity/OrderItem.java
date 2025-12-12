package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // A qué orden pertenece esta entrada

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime; // A qué función corresponde esta entrada


    // Relación directa con Seat
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_seat"))
    private Seat seat; // Qué asiento específico se compró en el showtime

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Precio individual de la entrada

    @Column(name = "ticket_type", length = 50)
    private String ticketType; // Tipo de entrada: ADULTO, NIÑO, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false)
    private TicketStatus ticketStatus; // ENUM: VALID, USED, CANCELLED

    @Column(name = "qr_code_ticket_url", length = 255)
    private String qrCodeTicketUrl; // URL del código QR específico para esta entrada

    @Column(name = "ticket_pdf_url", length = 255)
    private String ticketPdfUrl; // URL del PDF de la entrada individual
}