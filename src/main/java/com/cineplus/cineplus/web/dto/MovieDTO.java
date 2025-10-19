package com.cineplus.cineplus.web.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MovieDTO {
    private Long id;
    private String title;
    private String synopsis;
    private Integer durationMin;
    private String posterUrl;
}