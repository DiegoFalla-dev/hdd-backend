package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.TheaterDto;
import java.util.List;
import java.util.Optional;

public interface TheaterService {
    List<TheaterDto> findAllTheaters();
    List<TheaterDto> findTheatersByCinemaId(Long cinemaId);
    Optional<TheaterDto> findTheaterById(Long id);
    TheaterDto saveTheater(TheaterDto theaterDto);
    void deleteTheater(Long id);
}