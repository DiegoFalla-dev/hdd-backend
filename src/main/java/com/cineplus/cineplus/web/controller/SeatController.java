package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.service.SeatService;
import com.cineplus.cineplus.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/api/showtimes/{id}/seats")
    public List<SeatDTO> seats(@PathVariable Long id) {
        return seatService.listByShow(id).stream().map(s -> new SeatDTO(
                s.getId(), s.getRowLabel(), s.getSeatNumber(), s.getType().name(), s.getStatus().name()
        )).collect(Collectors.toList());
    }

    @PostMapping("/api/seats/reserve")
    public List<String> reserve(@RequestBody ReserveSeatsRequest req) {
        return seatService.reserveSeats(req.getShowId(), req.getSeatIds(), req.getHolderId(), req.getHoldSeconds());
    }
}