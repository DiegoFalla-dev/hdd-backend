package com.cineplus.cineplus.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderItemDTO {

    @NotNull(message = "El ID de la función no puede ser nulo")
    private Long showtimeId;

    @NotNull(message = "El ID del asiento no puede ser nulo")
    private Long seatId;

    @NotNull(message = "El precio del item no puede ser nulo")
    @Positive(message = "El precio del item debe ser un valor positivo")
    private BigDecimal price; // El precio que el cliente espera pagar por este item

    // Tipo de entrada (opcional). Ej: ADULT, CHILD, SENIOR, VIP
    private String ticketType;
}