package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.ShowtimeSeatDto;
import com.cineplus.cineplus.domain.entity.ShowtimeSeat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShowtimeSeatMapper {
    ShowtimeSeatMapper INSTANCE = Mappers.getMapper(ShowtimeSeatMapper.class);

    @Mapping(target = "showtimeId", source = "movieShowtime.id")
    @Mapping(target = "seatId", source = "seat.id")
    ShowtimeSeatDto toDto(ShowtimeSeat showtimeSeat);

    List<ShowtimeSeatDto> toDtoList(List<ShowtimeSeat> showtimeSeats);
}
