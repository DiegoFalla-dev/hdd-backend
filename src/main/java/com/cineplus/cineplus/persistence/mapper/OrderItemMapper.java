package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.CreateOrderItemDTO;
import com.cineplus.cineplus.domain.dto.OrderItemDTO;
import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.OrderItem;
import com.cineplus.cineplus.domain.entity.ShowtimeSeat;
import com.cineplus.cineplus.domain.entity.Showtime;
import com.cineplus.cineplus.domain.entity.TicketStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ShowtimeMapper.class, ShowtimeSeatMapper.class})
public interface OrderItemMapper {

    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    // Mapeo de Entidad a DTO para mostrar un item de orden
    @Mapping(target = "orderId", source = "order.id") // Solo el ID de la orden para evitar recursión
    @Mapping(target = "showtime", source = "showtime")
    @Mapping(target = "showtimeSeat", source = "showtimeSeat")
    OrderItemDTO toDto(OrderItem orderItem);

    // Mapeo de una lista de Entidades a DTOs
    List<OrderItemDTO> toDtoList(List<OrderItem> orderItems);

    @Mapping(target = "id", ignore = true) // El ID se genera en la DB
    @Mapping(target = "order", source = "order") // La orden se asigna en el servicio o un @AfterMapping
    @Mapping(target = "showtime", source = "showtime")
    @Mapping(target = "showtimeSeat", source = "showtimeSeat")
    @Mapping(target = "ticketStatus", expression = "java(com.cineplus.cineplus.domain.entity.TicketStatus.VALID)") // Estado inicial
    @Mapping(target = "qrCodeTicketUrl", ignore = true) // Se generará en el servicio
    @Mapping(target = "ticketPdfUrl", ignore = true) // Se generará en el servicio
    @Mapping(target = "price", source = "createOrderItemDTO.price")
    OrderItem toEntity(CreateOrderItemDTO createOrderItemDTO, Order order, Showtime showtime, ShowtimeSeat showtimeSeat);
}