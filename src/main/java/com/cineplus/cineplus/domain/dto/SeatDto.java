package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Long id;
    private Long showtimeId;
    private String seatIdentifier;
    private String code; // Código del asiento (alias de seatIdentifier para frontend)
    private String row; // Letra de la fila (A, B, C, etc.)
    private Integer number; // Número del asiento
    private String status; // AVAILABLE, OCCUPIED, TEMPORARILY_RESERVED
}