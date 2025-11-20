package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.SeatDto;
import com.cineplus.cineplus.domain.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    @Mapping(source = "seatIdentifier", target = "label")
    @Mapping(source = "status", target = "state")
    @Mapping(target = "row", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "heldBy", ignore = true)
    SeatDto toDto(Seat seat);

    List<SeatDto> toDtoList(List<Seat> seats);
}
