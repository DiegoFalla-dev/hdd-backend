package com.cineplus.cineplus.domain.dto;

// using String for state to decouple from entity enum
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Long id;
    private String row;
    private Integer number;
    private String label;
    private String state;
    private Long heldBy; // optional user id who holds it
}
