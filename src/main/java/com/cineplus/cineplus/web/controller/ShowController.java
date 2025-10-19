package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.persistance.repository.ShowRepository;
import com.cineplus.cineplus.web.dto.ShowDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/showtimes")
@RequiredArgsConstructor
public class ShowController {
    private final ShowRepository showRepository;

    @GetMapping
    public List<ShowDTO> list(@RequestParam(required = false) String cinemaExternalId) {
        // simple mapping; implement filters as needed
        return showRepository.findAll().stream().map(s -> new ShowDTO(
                s.getId(), s.getMovie().getId(), s.getHall().getId(), s.getStartTime(), s.getFormat(), s.getBasePriceCents()
        )).collect(Collectors.toList());
    }
}