package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.MovieDto;
import com.cineplus.cineplus.domain.entity.Movie;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieMapper {
    @Mapping(source = "cardImageUrl", target = "cardImageUrl")
    @Mapping(source = "bannerUrl", target = "bannerUrl")
    MovieDto toDto(Movie movie);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true) // El ID lo maneja la base de datos
    Movie toEntity(MovieDto movieDto);
}