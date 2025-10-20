package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.CinemaDto;
import com.cineplus.cineplus.domain.entity.Cinema;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CinemaMapper {
    CinemaDto toDto(Cinema cinema);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    //@Mapping(target = "movies", ignore = true) // Si Cinema tuviera una lista de películas
    Cinema toEntity(CinemaDto cinemaDto);
}