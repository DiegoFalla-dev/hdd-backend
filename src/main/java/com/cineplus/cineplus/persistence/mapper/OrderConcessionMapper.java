package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.OrderConcessionDTO;
import com.cineplus.cineplus.domain.entity.OrderConcession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderConcessionMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderConcessionDTO toDto(OrderConcession orderConcession);

    List<OrderConcessionDTO> toDtoList(List<OrderConcession> orderConcessions);
}
