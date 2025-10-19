package com.cineplus.cineplus.web.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class ShowDTO {
    private Long id;
    private Long movieId;
    private Long hallId;
    private OffsetDateTime startTime;
    private String format;
    private Integer basePriceCents;
}