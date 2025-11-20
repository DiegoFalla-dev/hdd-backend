package com.cineplus.cineplus.domain.dto;

import com.cineplus.cineplus.domain.entity.ReservationStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservationDto {
    private Long id;
    private Long showtimeId;
    private Long seatId;
    private Long userId;
    private ReservationStatus status;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
