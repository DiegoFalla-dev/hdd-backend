package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.TicketPriceDto;
import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.TicketPrice;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TicketPriceMapper {

    public TicketPrice toEntity(TicketPriceDto dto, Order order) {
        if (dto == null) return null;
        return TicketPrice.builder()
                .id(dto.getId())
                .order(order)
                .showtimeId(dto.getShowtimeId())
                .ticketType(dto.getTicketType())
                .seatCode(dto.getSeatCode())
                .unitPrice(dto.getUnitPrice())
                .quantity(dto.getQuantity() == null ? 1 : dto.getQuantity())
                .subtotal(dto.getSubtotal() == null ? (dto.getUnitPrice() == null ? null : dto.getUnitPrice().multiply(java.math.BigDecimal.valueOf(dto.getQuantity() == null ? 1 : dto.getQuantity()))) : dto.getSubtotal())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public TicketPriceDto toDto(TicketPrice entity) {
        if (entity == null) return null;
        return TicketPriceDto.builder()
                .id(entity.getId())
                .orderId(entity.getOrder() != null ? entity.getOrder().getId() : null)
                .showtimeId(entity.getShowtimeId())
                .ticketType(entity.getTicketType())
                .seatCode(entity.getSeatCode())
                .unitPrice(entity.getUnitPrice())
                .quantity(entity.getQuantity())
                .subtotal(entity.getSubtotal())
                .build();
    }
}
