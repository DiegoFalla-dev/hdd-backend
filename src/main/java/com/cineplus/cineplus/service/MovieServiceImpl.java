package com.cineplus.cineplus.service;

import com.cineplus.cineplus.persistance.repository.MovieRepository;
import com.cineplus.cineplus.web.dto.MovieDTO;
import com.cineplus.cineplus.persistance.entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class MovieServiceImpl implements MovieService{
    private final MovieRepository repo;

    @Override
    public List<MovieDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public MovieDTO findById(Long id) {
        Movie m = repo.findById(id).orElseThrow();
        return toDto(m);
    }

    private MovieDTO toDto(Movie m) {
        return new MovieDTO(m.getId(), m.getTitle(), m.getSynopsis(), m.getDurationMin(), m.getPosterUrl());
    }
}
