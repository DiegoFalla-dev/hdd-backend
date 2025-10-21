package com.cineplus.cineplus.domain.dto;

import com.cineplus.cineplus.domain.entity.Theater;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TheaterDto {
    private Long id;
    private Long cinemaId;
    private String cinemaName;
    private String name;
    private Theater.SeatMatrixType seatMatrixType;
    private int rowCount;
    private int colCount;
    private int totalSeats;
}