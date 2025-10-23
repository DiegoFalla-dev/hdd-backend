package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.CinemaDto;
import com.cineplus.cineplus.domain.entity.Cinema;
import com.cineplus.cineplus.domain.repository.CinemaRepository;
import com.cineplus.cineplus.domain.service.CinemaService;
import com.cineplus.cineplus.persistence.mapper.CinemaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {

    private final CinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CinemaDto> findAllCinemas() {
        return cinemaRepository.findAll().stream()
                .map(cinemaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CinemaDto> findCinemaById(Long id) {
        return cinemaRepository.findById(id)
                .map(cinemaMapper::toDto);
    }

    @Override
    @Transactional
    public CinemaDto saveCinema(CinemaDto cinemaDto) {
        Cinema cinema = cinemaMapper.toEntity(cinemaDto);
        Cinema savedCinema = cinemaRepository.save(cinema);
        return cinemaMapper.toDto(savedCinema);
    }

    @Override
    @Transactional
    public void deleteCinema(Long id) {
        cinemaRepository.deleteById(id);
    }
}