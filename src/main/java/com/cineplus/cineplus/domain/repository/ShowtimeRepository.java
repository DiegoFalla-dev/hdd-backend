package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByTheaterCinemaIdAndMovieIdAndDate(Long cinemaId, Long movieId, LocalDate date);

    @Query("SELECT DISTINCT s.date FROM Showtime s WHERE s.theater.cinema.id = :cinemaId AND s.movie.id = :movieId AND s.date >= :currentDate ORDER BY s.date")
    List<LocalDate> findDistinctDatesByCinemaIdAndMovieIdAfter(@Param("cinemaId") Long cinemaId, @Param("movieId") Long movieId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT s FROM Showtime s WHERE s.theater.cinema.id = :cinemaId AND s.movie.id = :movieId AND s.date = :date AND s.format = :format AND (s.date > :currentDate OR (s.date = :currentDate AND s.time > :currentTime)) ORDER BY s.time")
    List<Showtime> findAvailableShowtimes(
            @Param("cinemaId") Long cinemaId,
            @Param("movieId") Long movieId,
            @Param("date") LocalDate date,
            @Param("format") Showtime.FormatType format,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime
    );

    Optional<Showtime> findByIdAndTheaterCinemaId(Long showtimeId, Long cinemaId);

    // Buscar showtimes por movieId con filtros opcionales de cinemaId y date
    @Query("SELECT s FROM Showtime s " +
           "WHERE s.movie.id = :movieId " +
           "AND (:cinemaId IS NULL OR s.theater.cinema.id = :cinemaId) " +
           "AND (:date IS NULL OR s.date = :date) " +
           "ORDER BY s.date, s.time")
    List<Showtime> findShowtimesByFilters(
            @Param("movieId") Long movieId,
            @Param("cinemaId") Long cinemaId,
            @Param("date") LocalDate date
    );
}