package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.TheaterDto;
import com.cineplus.cineplus.domain.entity.Cinema;
import com.cineplus.cineplus.domain.entity.Theater;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T00:45:44-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class TheaterMapperImpl implements TheaterMapper {

    @Override
    public TheaterDto toDto(Theater theater) {
        if ( theater == null ) {
            return null;
        }

        TheaterDto theaterDto = new TheaterDto();

        theaterDto.setCinemaId( theaterCinemaId( theater ) );
        theaterDto.setCinemaName( theaterCinemaName( theater ) );
        if ( theater.getSeatMatrixType() != null ) {
            theaterDto.setSeatMatrixType( Enum.valueOf( Theater.SeatMatrixType.class, mapSeatMatrixTypeToString( theater.getSeatMatrixType() ) ) );
        }
        theaterDto.setRowCount( theater.getRowCount() );
        theaterDto.setColCount( theater.getColCount() );
        theaterDto.setId( theater.getId() );
        theaterDto.setName( theater.getName() );
        theaterDto.setTotalSeats( theater.getTotalSeats() );

        return theaterDto;
    }

    @Override
    public Theater toEntity(TheaterDto theaterDto) {
        if ( theaterDto == null ) {
            return null;
        }

        Theater theater = new Theater();

        if ( theaterDto.getSeatMatrixType() != null ) {
            theater.setSeatMatrixType( mapStringToSeatMatrixType( theaterDto.getSeatMatrixType().name() ) );
        }
        theater.setRowCount( theaterDto.getRowCount() );
        theater.setColCount( theaterDto.getColCount() );
        theater.setName( theaterDto.getName() );

        return theater;
    }

    private Long theaterCinemaId(Theater theater) {
        if ( theater == null ) {
            return null;
        }
        Cinema cinema = theater.getCinema();
        if ( cinema == null ) {
            return null;
        }
        Long id = cinema.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String theaterCinemaName(Theater theater) {
        if ( theater == null ) {
            return null;
        }
        Cinema cinema = theater.getCinema();
        if ( cinema == null ) {
            return null;
        }
        String name = cinema.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
