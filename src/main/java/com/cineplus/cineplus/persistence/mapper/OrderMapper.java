package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.CreateOrderDTO;
import com.cineplus.cineplus.domain.dto.OrderDTO;
import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.entity.PaymentMethod;
import com.cineplus.cineplus.domain.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, PaymentMethodMapper.class, PromotionMapper.class, OrderItemMapper.class, OrderConcessionMapper.class})
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    // Mapeo de Entidad a DTO para mostrar una orden existente
    @Mapping(target = "user", source = "user")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedBy = PaymentMethodMapping.class)
    @Mapping(target = "promotion", source = "promotion")
    @Mapping(target = "discountAmount", source = "discountAmount")
    @Mapping(target = "fidelityDiscountAmount", source = "fidelityDiscountAmount")
    @Mapping(target = "orderItems", source = "orderItems")
    @Mapping(target = "orderConcessions", source = "orderConcessions")
    OrderDTO toDto(Order order);

    // Mapeo de una lista de Entidades a DTOs
    List<OrderDTO> toDtoList(List<Order> orders);


    @Mapping(target = "id", ignore = true) // El ID se genera en la DB
    @Mapping(target = "orderItems", ignore = true) // Los orderItems se mapearán por separado en el servicio
    @Mapping(target = "orderConcessions", ignore = true) // Las concesiones se mapearán por separado en el servicio
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())") // Fecha actual
    @Mapping(target = "orderStatus", expression = "java(com.cineplus.cineplus.domain.entity.OrderStatus.PENDING)") // Estado inicial
    @Mapping(target = "totalAmount", ignore = true) // Se calculará en el servicio
    @Mapping(target = "invoiceNumber", ignore = true) // Se generará en el servicio
    @Mapping(target = "invoicePdfUrl", ignore = true) // Se generará en el servicio
    @Mapping(target = "qrCodeUrl", ignore = true) // Se generará en el servicio
    Order toEntity(CreateOrderDTO createOrderDTO);
}