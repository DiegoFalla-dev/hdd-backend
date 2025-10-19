package com.cineplus.cineplus.web.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class SeatDTO {
    private Long id;
    private String row;
    private Integer number;
    private String type;
    private String status;
}
