package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.ShowtimeDto;
import com.cineplus.cineplus.domain.entity.Showtime;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ShowtimeService {
    List<ShowtimeDto> getAvailableShowtimeDates(Long cinemaId, Long movieId);
    List<ShowtimeDto> getMovieShowtimes(Long cinemaId, Long movieId, LocalDate date, Showtime.FormatType format);
    Optional<ShowtimeDto> getShowtimeDetails(Long showtimeId, Long cinemaId);
    void generateSeatsForShowtime(Long showtimeId); // Para inicializar asientos
    List<String> getOccupiedSeats(Long showtimeId); // Obtener asientos ocupados para el frontend
    List<String> reserveSeatsTemporarily(Long showtimeId, Set<String> seatIdentifiers); // Retorna los asientos que no se pudieron reservar
    void releaseTemporaryReservedSeats(Long showtimeId, Set<String> seatIdentifiers); // Libera asientos temporales
    void confirmSeatsAsOccupied(Long showtimeId, Set<String> seatIdentifiers); // Confirma asientos

        // Obtener todas las funciones de una fecha (todos los formatos) — útil para listar formatos disponibles
        List<ShowtimeDto> getShowtimesByDate(Long cinemaId, Long movieId, LocalDate date);
    ShowtimeDto saveShowtime (ShowtimeDto showtimeDto);
}