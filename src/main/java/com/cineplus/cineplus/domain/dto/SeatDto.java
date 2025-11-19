package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDto {
    private Long id;
    private String seatNumber; // Ej: "A1", "B5"
    private Boolean isAvailable; // Podría ser útil
    private Long theaterId; // Añadir el ID del teatro al que pertenece el asiento
    private String theaterName; // Nombre del teatro
    private String seatRow; // Fila del asiento (ej: "A")
    private Integer seatColumn; // Columna del asiento (ej: 1)
}