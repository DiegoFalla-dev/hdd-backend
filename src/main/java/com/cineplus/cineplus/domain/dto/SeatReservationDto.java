package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservationDto {
    private String sessionId;
    private Long showtimeId;
    private Set<String> seatIdentifiers;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;
    private Boolean isActive;
    private Boolean isConfirmed;
    private String purchaseNumber;
    private Long userId;
}
