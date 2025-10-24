package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.PaymentMethodDto; // Asumo que ya tienes o crearás este DTO
import com.cineplus.cineplus.domain.entity.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethodMapper INSTANCE = Mappers.getMapper(PaymentMethodMapper.class);

    @PaymentMethodMapping
    PaymentMethodDto toDto(PaymentMethod paymentMethod);
    PaymentMethod toEntity(PaymentMethodDto paymentMethodDto);
    List<PaymentMethodDto> toDtoList(List<PaymentMethod> paymentMethods);
    List<PaymentMethod> toEntityList(List<PaymentMethodDto> paymentMethodDtos);
}