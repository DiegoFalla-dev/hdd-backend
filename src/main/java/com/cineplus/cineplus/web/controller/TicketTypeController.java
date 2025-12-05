package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.TicketTypeDto;
import com.cineplus.cineplus.domain.service.TicketTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-types")
@RequiredArgsConstructor
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @GetMapping
    public ResponseEntity<List<TicketTypeDto>> getAll() {
        return ResponseEntity.ok(ticketTypeService.findAll());
    }
}
