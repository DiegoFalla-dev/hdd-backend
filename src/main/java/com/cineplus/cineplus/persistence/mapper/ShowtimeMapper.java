package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.ShowtimeDto;
import com.cineplus.cineplus.domain.entity.Showtime;
import com.cineplus.cineplus.domain.entity.Theater.SeatMatrixType; // Importa SeatMatrixType
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShowtimeMapper {

    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.title", target = "movieTitle")
    @Mapping(source = "theater.id", target = "theaterId")
    @Mapping(source = "theater.name", target = "theaterName")
    @Mapping(source = "theater.cinema.id", target = "cinemaId")
    @Mapping(source = "theater.cinema.name", target = "cinemaName")
    @Mapping(source = "theater.totalSeats", target = "totalSeats")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "theater.seatMatrixType", target = "seatMatrixType", qualifiedByName = "mapSeatMatrixTypeToString")
    ShowtimeDto toDto(Showtime showtime);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movie", ignore = true)     // Se seteará manualmente o en un servicio
    @Mapping(target = "theater", ignore = true)   // Se seteará manualmente o en un servicio
    Showtime toEntity(ShowtimeDto showtimeDto);

    @Named("mapSeatMatrixTypeToString")
    default String mapSeatMatrixTypeToString(SeatMatrixType seatMatrixType) {
        return seatMatrixType != null ? seatMatrixType.name() : null;
    }
}