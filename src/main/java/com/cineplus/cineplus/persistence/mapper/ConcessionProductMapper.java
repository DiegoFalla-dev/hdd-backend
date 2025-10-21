package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.ConcessionProductDto;
import com.cineplus.cineplus.domain.entity.Cinema;
import com.cineplus.cineplus.domain.entity.ConcessionProduct;
import org.mapstruct.*;

import java.util.Collections; // Importa esto
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConcessionProductMapper {

    @Mapping(source = "cinemas", target = "cinemaIds", qualifiedByName = "cinemasToCinemaIds")
    ConcessionProductDto toDto(ConcessionProduct concessionProduct);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cinemas", ignore = true) // Se seteará manualmente en el servicio
    ConcessionProduct toEntity(ConcessionProductDto concessionProductDto);

    @Named("cinemasToCinemaIds")
    default Set<Long> cinemasToCinemaIds(Set<Cinema> cinemas) {
        // Añade esta verificación de nulidad
        if (cinemas == null) {
            return Collections.emptySet(); // Devuelve un conjunto vacío en lugar de null
        }
        return cinemas.stream()
                .map(Cinema::getId)
                .collect(Collectors.toSet());
    }
}