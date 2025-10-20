package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.CinemaDto;
import java.util.List;
import java.util.Optional;

public interface CinemaService {
    List<CinemaDto> findAllCinemas();
    Optional<CinemaDto> findCinemaById(Long id);
    CinemaDto saveCinema(CinemaDto cinemaDto);
    void deleteCinema(Long id);
}