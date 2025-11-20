package com.cineplus.cineplus.domain.dto;

import com.cineplus.cineplus.domain.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private Long id;
    private Long orderId; // Solo el ID de la orden para evitar recursión infinita
    private ShowtimeDto showtime;
    private SeatDto seat;
    private BigDecimal price;
    private TicketStatus ticketStatus;
    private String qrCodeTicketUrl;
    private String ticketPdfUrl;
}