package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType; // ENUM: PERCENTAGE, FIXED_AMOUNT

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal value; // Ej: 0.10 para 10%, o 5.00 para $5

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "min_amount", precision = 10, scale = 2)
    private BigDecimal minAmount; // Monto mínimo de compra para aplicar el descuento

    @Column(name = "max_uses")
    private Integer maxUses; // Límite de usos del descuento

    @Column(name = "current_uses")
    private Integer currentUses = 0; // Contador de usos actuales

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relación con Order (si un descuento se aplica a una orden, puede ser 1 a M)
    // Pero si un descuento puede aplicarse a muchas órdenes, y una orden puede tener varios descuentos, sería M a M
    // Para simplificar, por ahora no añadimos la lista de órdenes directamente aquí.
    // La relación se gestionará a través de Order (order_discounts o directamente en Order si es 1-1)
}