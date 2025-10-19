package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.service.MovieService;
import com.cineplus.cineplus.web.dto.MovieDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public List<MovieDTO> list() {
        return movieService.findAll();
    }

    @GetMapping("/{id}")
    public MovieDTO get(@PathVariable Long id){
        return movieService.findById(id);
    }
}
