package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.ShowtimeDto;
import com.cineplus.cineplus.domain.entity.Cinema;
import com.cineplus.cineplus.domain.entity.Movie;
import com.cineplus.cineplus.domain.entity.Seat;
import com.cineplus.cineplus.domain.entity.Seat.SeatStatus;
import com.cineplus.cineplus.domain.entity.Showtime;
import com.cineplus.cineplus.domain.entity.Theater;
import com.cineplus.cineplus.domain.repository.CinemaRepository;
import com.cineplus.cineplus.domain.repository.MovieRepository;
import com.cineplus.cineplus.domain.repository.SeatRepository;
import com.cineplus.cineplus.domain.repository.ShowtimeRepository;
import com.cineplus.cineplus.domain.repository.TheaterRepository;
import com.cineplus.cineplus.domain.service.ShowtimeService;
import com.cineplus.cineplus.persistence.mapper.ShowtimeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository; // Para validaciones
    private final ShowtimeMapper showtimeMapper;

    // --- Métodos de consulta ---
    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeDto> getAvailableShowtimeDates(Long cinemaId, Long movieId) {
        LocalDate currentDate = LocalDate.now();
        List<LocalDate> dates = showtimeRepository.findDistinctDatesByCinemaIdAndMovieIdAfter(cinemaId, movieId, currentDate);
        // Podrías mapear esto a un DTO de fechas si fuera necesario, o devolver List<LocalDate>
        // Por simplicidad, aquí solo devolvemos las fechas, pero la interfaz de servicio espera ShowtimeDto
        // Esto puede requerir un ajuste en la interfaz o un DTO específico para fechas.
        // Por ahora, devolveremos una lista vacía y lo ajustaremos cuando haya un DTO de fecha si es necesario.
        return dates.stream()
                .map(date -> {
                    ShowtimeDto dto = new ShowtimeDto();
                    dto.setDate(date);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowtimeDto> getMovieShowtimes(Long cinemaId, Long movieId, LocalDate date, Showtime.FormatType format) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        return showtimeRepository.findAvailableShowtimes(cinemaId, movieId, date, format, currentDate, currentTime)
                .stream()
                .map(showtimeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShowtimeDto> getShowtimeDetails(Long showtimeId, Long cinemaId) {
        return showtimeRepository.findByIdAndTheaterCinemaId(showtimeId, cinemaId)
                .map(showtimeMapper::toDto);
    }

    // --- Métodos de gestión de asientos ---
    @Override
    @Transactional
    public void generateSeatsForShowtime(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found with id: " + showtimeId));

        Theater theater = showtime.getTheater();
        if (theater == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Showtime has no associated theater.");
        }

        // Si ya tiene asientos, no los generamos de nuevo
        if (!seatRepository.findByShowtimeId(showtimeId).isEmpty()) {
            return;
        }

        List<Seat> seatsToGenerate = new ArrayList<>();
        char rowChar = 'A';
        for (int r = 0; r < theater.getRowCount(); r++) {
            for (int c = 0; c < theater.getColCount(); c++) {
                String seatIdentifier = String.valueOf(rowChar) + (c + 1);
                Seat seat = new Seat(null, showtime, seatIdentifier, SeatStatus.AVAILABLE, r, c);
                seatsToGenerate.add(seat);
            }
            rowChar++;
        }
        seatRepository.saveAll(seatsToGenerate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getOccupiedSeats(Long showtimeId) {
        return seatRepository.findByShowtimeId(showtimeId).stream()
                .filter(seat -> seat.getStatus().equals(SeatStatus.OCCUPIED) || seat.getStatus().equals(SeatStatus.TEMPORARILY_RESERVED))
                .map(Seat::getSeatIdentifier)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<String> reserveSeatsTemporarily(Long showtimeId, Set<String> seatIdentifiers) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found."));

        // Intentar actualizar el estado de los asientos de AVAILABLE a TEMPORARILY_RESERVED
        int updatedCount = seatRepository.updateSeatStatusIfExpected(showtimeId, seatIdentifiers, SeatStatus.TEMPORARILY_RESERVED, SeatStatus.AVAILABLE);

        if (updatedCount != seatIdentifiers.size()) {
            // Si no se actualizaron todos, significa que algunos ya no estaban AVAILABLE
            List<Seat> currentSeats = seatRepository.findByShowtimeIdAndSeatIdentifierIn(showtimeId, seatIdentifiers);
            Set<String> successfullyReserved = currentSeats.stream()
                    .filter(seat -> seat.getStatus().equals(SeatStatus.TEMPORARILY_RESERVED))
                    .map(Seat::getSeatIdentifier)
                    .collect(Collectors.toSet());

            Set<String> failedToReserve = seatIdentifiers.stream()
                    .filter(id -> !successfullyReserved.contains(id))
                    .collect(Collectors.toSet());

            // Devolver los que fallaron
            return new ArrayList<>(failedToReserve);
        }

        // Actualizar el contador de asientos disponibles en el showtime
        showtime.setAvailableSeats(showtime.getAvailableSeats() - seatIdentifiers.size());
        showtimeRepository.save(showtime);

        return new ArrayList<>(); // Todos se reservaron correctamente
    }

    @Override
    @Transactional
    public void releaseTemporaryReservedSeats(Long showtimeId, Set<String> seatIdentifiers) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found."));

        // Actualizar el estado de los asientos de TEMPORARILY_RESERVED a AVAILABLE
        seatRepository.updateSeatStatusIfExpected(showtimeId, seatIdentifiers, SeatStatus.AVAILABLE, SeatStatus.TEMPORARILY_RESERVED);

        // Actualizar el contador de asientos disponibles en el showtime
        showtime.setAvailableSeats(showtime.getAvailableSeats() + seatIdentifiers.size());
        showtimeRepository.save(showtime);
    }

    @Override
    @Transactional
    public void confirmSeatsAsOccupied(Long showtimeId, Set<String> seatIdentifiers) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found."));

        // Actualizar el estado de los asientos de TEMPORARILY_RESERVED a OCCUPIED
        int updatedCount = seatRepository.updateSeatStatusIfExpected(showtimeId, seatIdentifiers, SeatStatus.OCCUPIED, SeatStatus.TEMPORARILY_RESERVED);

        if (updatedCount != seatIdentifiers.size()) {
            // Esto es un error grave, significa que algunos asientos reservados temporalmente ya no lo estaban.
            // Podrías lanzar una excepción o registrar el evento.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Some seats could not be confirmed as occupied.");
        }
        // Nota: availableSeats ya se actualizó en reserveSeatsTemporarily, aquí solo cambiamos el estado final.
    }

    @Override
    @Transactional
    public ShowtimeDto saveShowtime(ShowtimeDto showtimeDto) {
        // Validar que la película y la sala existan
        Movie movie = movieRepository.findById(showtimeDto.getMovieId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found with id: " + showtimeDto.getMovieId()));
        Theater theater = theaterRepository.findById(showtimeDto.getTheaterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Theater not found with id: " + showtimeDto.getTheaterId()));

        Showtime showtime = showtimeMapper.toEntity(showtimeDto);
        showtime.setMovie(movie);
        showtime.setTheater(theater);

        // Inicializa availableSeats con el total de asientos de la sala
        showtime.setAvailableSeats(theater.getTotalSeats());

        Showtime savedShowtime = showtimeRepository.save(showtime);
        return showtimeMapper.toDto(savedShowtime);
    }
}