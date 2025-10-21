package com.cineplus.cineplus.domain.dto;

import com.cineplus.cineplus.domain.entity.Showtime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeDto {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long theaterId;
    private String theaterName;
    private Long cinemaId; // Añadir ID del cine para facilitar el frontend
    private String cinemaName; // Añadir nombre del cine para facilitar el frontend
    private LocalDate date;
    private LocalTime time;
    private Showtime.FormatType format;
    private int availableSeats;
    private int totalSeats; // Se obtendrá del teatro asociado
    private String seatMatrixType; // e.g., "SMALL", "MEDIUM"
}