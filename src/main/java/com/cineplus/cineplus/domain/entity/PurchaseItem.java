package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad que representa un ítem individual dentro de una compra.
 * Puede ser una entrada de cine o un producto de confitería.
 */
@Entity
@Table(name = "purchase_items")
@Getter
@Setter
@NoArgsConstructor
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Compra a la que pertenece este ítem
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    /**
     * Tipo de ítem (TICKET o CONCESSION)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private PurchaseItemType itemType;

    /**
     * Descripción del ítem
     * Ejemplo: "Entrada General - Los 4 Fantásticos", "Combo Familiar", "Canchita Grande"
     */
    @Column(name = "description", nullable = false, length = 255)
    private String description;

    /**
     * Cantidad de unidades
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Precio unitario en soles (PEN)
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Subtotal = quantity * unitPrice
     */
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * ID del producto de confitería (si aplica)
     * Null para entradas de cine
     */
    @Column(name = "concession_product_id")
    private Long concessionProductId;

    /**
     * Identificadores de asientos (si es entrada de cine)
     * Formato: "A1,A2,A3" o null si no aplica
     */
    @Column(name = "seat_identifiers", length = 500)
    private String seatIdentifiers;

    /**
     * Calcula automáticamente el subtotal
     */
    public void calculateSubtotal() {
        if (quantity != null && unitPrice != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
