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
public class TicketPriceDto {
    private Long id;
    private Long orderId;
    private Long showtimeId;
    private String ticketType;
    private String seatCode;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}
