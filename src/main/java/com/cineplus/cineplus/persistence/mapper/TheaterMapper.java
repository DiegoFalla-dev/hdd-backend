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
    // Mantener 'rowCount' como target si cambiaste 'rows' en la entidad
    @Mapping(source = "rowCount", target = "rowCount")
    // Asegurarse de que 'cols' se mapea directamente si el nombre del campo no cambió en la entidad
    @Mapping(source = "colCount", target = "colCount")
    TheaterDto toDto(Theater theater);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cinema", ignore = true) // Se establecerá en el servicio
    @Mapping(target = "totalSeats", ignore = true) // Se calculará en el servicio
    @Mapping(source = "seatMatrixType", target = "seatMatrixType", qualifiedByName = "mapStringToSeatMatrixType")
    // Mantener 'rowCount' como source si cambiaste 'rows' en la entidad
    @Mapping(target = "rowCount", source = "rowCount")
    // Asegurarse de que 'cols' se mapea directamente si el nombre del campo no cambió en la entidad
    @Mapping(target = "colCount", source = "colCount")
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