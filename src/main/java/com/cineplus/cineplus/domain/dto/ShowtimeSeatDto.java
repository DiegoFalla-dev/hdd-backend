package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeSeatDto {
    private Long id;
    private Long showtimeId;
    private Long seatId;
    private Boolean isAvailable;
}
