package com.cineplus.cineplus.domain.dto;

import com.cineplus.cineplus.domain.entity.Seat.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para representar un asiento con sus coordenadas en la matriz de la sala
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Long id;
    private String seatIdentifier;
    private SeatStatus status;
    private Integer rowPosition;
    private Integer colPosition;
    private Boolean isCancelled;
    private String sessionId;
    private String purchaseNumber;
}
