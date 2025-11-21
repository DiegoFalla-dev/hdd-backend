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
    private String status; // AVAILABLE, OCCUPIED, TEMPORARILY_RESERVED
}