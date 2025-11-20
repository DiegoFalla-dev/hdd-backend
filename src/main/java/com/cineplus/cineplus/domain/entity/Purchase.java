package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una compra/orden completada en el sistema.
 * Almacena información completa de la transacción incluyendo:
 * - Datos del usuario comprador
 * - Función de cine (showtime) asociada
 * - Método de pago utilizado
 * - Items comprados (entradas y/o confitería)
 * - Monto total y estado de la transacción
 */
@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número único de compra generado por el sistema
     * Formato: CIN-yyyyMMddHHmmss-XXXXXXXX
     * Ejemplo: CIN-20251120153045-A7B3C9D1
     */
    @Column(name = "purchase_number", unique = true, nullable = false, length = 50)
    private String purchaseNumber;

    /**
     * Usuario que realizó la compra
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Función de cine asociada (para compras de entradas)
     * Puede ser null si la compra es solo de confitería
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    /**
     * Método de pago utilizado
     * Puede ser null si el pago fue en efectivo o por otro medio no registrado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    /**
     * Monto total de la compra en soles (PEN)
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Fecha y hora en que se completó la compra
     */
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    /**
     * Estado actual de la compra
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PurchaseStatus status;

    /**
     * ID de transacción retornado por la pasarela de pago (opcional)
     * Se guarda para trazabilidad y reconciliación
     */
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    /**
     * ID de sesión de reserva temporal asociada
     * Se guarda para trazabilidad del proceso de compra
     */
    @Column(name = "session_id", length = 50)
    private String sessionId;

    /**
     * Items comprados (entradas, confitería, etc.)
     * Relación one-to-many con cascada para guardar automáticamente
     */
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();

    /**
     * Añade un item a la compra
     */
    public void addItem(PurchaseItem item) {
        items.add(item);
        item.setPurchase(this);
    }

    /**
     * Remueve un item de la compra
     */
    public void removeItem(PurchaseItem item) {
        items.remove(item);
        item.setPurchase(null);
    }
}
