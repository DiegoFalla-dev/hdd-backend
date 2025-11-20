package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * DTO para solicitar reserva de asientos
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveSeatRequestDto {
    private Set<String> seatIdentifiers;
    private Long userId; // Opcional
}
