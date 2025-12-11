package com.cineplus.cineplus.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDTO {

    @NotNull(message = "El ID del usuario no puede ser nulo")
    private Long userId;

    @NotNull(message = "El ID del método de pago no puede ser nulo")
    private Long paymentMethodId;

    @NotEmpty(message = "La orden debe contener al menos un item")
    @Valid // Para validar cada item de la lista
    private List<CreateOrderItemDTO> items;

    @Valid
    private List<CreateOrderConcessionDTO> concessions; // Opcional: concesiones/dulcería

    private String promotionCode; // Opcional: para aplicar un código de promoción

    // Descuento por puntos de fidelización (opcional)
    // fidelityPointsRedeemed: puntos que el usuario ya canjeó en un endpoint previo
    // fidelityDiscountAmount: monto fijo en soles que debe descontarse al total (100 pts = S/10)
    private Integer fidelityPointsRedeemed;
    private java.math.BigDecimal fidelityDiscountAmount;
}