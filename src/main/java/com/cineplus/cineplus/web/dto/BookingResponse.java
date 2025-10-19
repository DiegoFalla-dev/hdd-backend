package com.cineplus.cineplus.web.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private String status;
    private Integer totalCents;
}