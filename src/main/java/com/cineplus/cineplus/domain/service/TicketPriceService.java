package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.TicketPriceDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TicketPriceService {
    List<TicketPriceDto> findByOrderId(Long orderId);

    /**
     * Guarda las líneas de precio de tickets asociadas a una orden.
     * @param orderId id de la orden a la que se asocian
     * @param items lista de TicketPriceDto (unitPrice, quantity, ticketType, showtimeId, seatCode)
     * @return lista persistida con ids
     */
    List<TicketPriceDto> saveForOrder(Long orderId, List<TicketPriceDto> items);

    Optional<BigDecimal> sumSubtotalByOrderId(Long orderId);
}
