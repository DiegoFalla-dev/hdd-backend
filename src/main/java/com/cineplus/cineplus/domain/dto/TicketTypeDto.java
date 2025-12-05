package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketTypeDto {
    private Long id;
    private String code;
    private String name;
    private BigDecimal price;
    private boolean active;
}
