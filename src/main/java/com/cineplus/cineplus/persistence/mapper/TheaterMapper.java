package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.TheaterDto;
import com.cineplus.cineplus.domain.entity.Theater;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TheaterMapper {

    @Mapping(source = "cinema.id", target = "cinemaId")
    @Mapping(source = "cinema.name", target = "cinemaName")
    @Mapping(source = "seatMatrixType", target = "seatMatrixType", qualifiedByName = "mapSeatMatrixTypeToString")
    TheaterDto toDto(Theater theater);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cinema", ignore = true) // Se establecerá en el servicio
    @Mapping(target = "totalSeats", ignore = true) // Se calculará en el servicio
    @Mapping(source = "seatMatrixType", target = "seatMatrixType", qualifiedByName = "mapStringToSeatMatrixType")
    Theater toEntity(TheaterDto theaterDto);

    @Named("mapSeatMatrixTypeToString")
    default String mapSeatMatrixTypeToString(Theater.SeatMatrixType seatMatrixType) {
        return seatMatrixType != null ? seatMatrixType.name() : null;
    }

    @Named("mapStringToSeatMatrixType")
    default Theater.SeatMatrixType mapStringToSeatMatrixType(String seatMatrixType) {
        return seatMatrixType != null ? Theater.SeatMatrixType.valueOf(seatMatrixType) : null;
    }
}