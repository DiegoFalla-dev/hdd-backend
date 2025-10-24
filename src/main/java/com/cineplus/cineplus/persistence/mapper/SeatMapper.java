package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.SeatDto; // Asumo que ya tienes o crearás este DTO
import com.cineplus.cineplus.domain.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    SeatMapper INSTANCE = Mappers.getMapper(SeatMapper.class);

    SeatDto toDto(Seat seat);
    Seat toEntity(SeatDto seatDto);
    List<SeatDto> toDtoList(List<Seat> seats);
    List<Seat> toEntityList(List<SeatDto> seatDtos);
}