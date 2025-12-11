package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final OrderRepository orderRepository;

    @GetMapping("/sales/daily")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> dailySales() {
        var rows = orderRepository.aggregateDailyTotals()
                .stream()
                .map(p -> Map.of(
                        "day", (Object) p.getDay(),
                        "total", (Object) p.getTotal()
                ))
                .toList();
        return ResponseEntity.ok(rows);
    }
}
