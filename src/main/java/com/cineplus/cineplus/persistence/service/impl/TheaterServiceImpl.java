package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.TheaterDto;
import com.cineplus.cineplus.domain.entity.Cinema;
import com.cineplus.cineplus.domain.entity.Theater;
import com.cineplus.cineplus.domain.repository.CinemaRepository;
import com.cineplus.cineplus.domain.repository.TheaterRepository;
import com.cineplus.cineplus.domain.service.TheaterService;
import com.cineplus.cineplus.persistence.mapper.TheaterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;
    private final CinemaRepository cinemaRepository; // Para asociar la sala a un cine existente
    private final TheaterMapper theaterMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TheaterDto> findAllTheaters() {
        return theaterRepository.findAll().stream()
                .map(theaterMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TheaterDto> findTheatersByCinemaId(Long cinemaId) {
        return theaterRepository.findByCinemaId(cinemaId).stream()
                .map(theaterMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TheaterDto> findTheaterById(Long id) {
        return theaterRepository.findById(id)
                .map(theaterMapper::toDto);
    }

    @Override
    @Transactional
    public TheaterDto saveTheater(TheaterDto theaterDto) {
        // Asegúrate de que el cine exista antes de guardar la sala
        Cinema cinema = cinemaRepository.findById(theaterDto.getCinemaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cinema not found with id: " + theaterDto.getCinemaId()));

        Theater theater = theaterMapper.toEntity(theaterDto);
        theater.setCinema(cinema); // Asocia el objeto Cinema real
        theater.setTotalSeats(theater.getRows() * theater.getCols()); // Calcula el total de asientos

        Theater savedTheater = theaterRepository.save(theater);
        return theaterMapper.toDto(savedTheater);
    }

    @Override
    @Transactional
    public void deleteTheater(Long id) {
        theaterRepository.deleteById(id);
    }
}