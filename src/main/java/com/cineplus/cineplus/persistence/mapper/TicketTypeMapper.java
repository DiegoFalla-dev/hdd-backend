package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.TicketTypeDto;
import com.cineplus.cineplus.domain.entity.TicketType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketTypeMapper {
    TicketTypeDto toDto(TicketType entity);
    TicketType toEntity(TicketTypeDto dto);
    List<TicketTypeDto> toDtoList(List<TicketType> entities);
}
