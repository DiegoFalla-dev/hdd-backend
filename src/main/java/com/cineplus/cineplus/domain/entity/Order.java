package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Quién realizó la compra

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "subtotal_amount", precision = 10, scale = 2)
    private BigDecimal subtotalAmount; // Subtotal SIN impuestos

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount; // IGV (18%)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod; // Método de pago utilizado

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus; // ENUM: PENDING, COMPLETED, CANCELLED, REFUNDED

    @Column(name = "invoice_number", unique = true, length = 255)
    private String invoiceNumber; // Número único de la factura

    @Column(name = "invoice_pdf_url", length = 255)
    private String invoicePdfUrl; // URL del PDF de la factura

    @Column(name = "qr_code_url", length = 255)
    private String qrCodeUrl; // URL del código QR general de la orden (si aplica)

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>(); // Las entradas individuales de esta orden

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderConcession> orderConcessions = new ArrayList<>(); // Concesiones (dulcería) de esta orden

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion; // Si solo se aplica un descuento por orden
}