package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.service.BookingService;
import com.cineplus.cineplus.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse create(@RequestBody BookingRequest req) {
        var booking = bookingService.confirmBooking(req.getUserExternalId(), req.getUserExternalId(), req.getShowId(), req.getSeatIds());
        return new BookingResponse(booking.getId(), booking.getStatus().name(), booking.getTotalCents());
    }
}