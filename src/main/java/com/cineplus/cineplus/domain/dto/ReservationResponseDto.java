package com.cineplus.cineplus.domain.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {
    private Long reservationId;
    private String token;
    private LocalDateTime expiresAt;
    private List<Long> seats;
}
