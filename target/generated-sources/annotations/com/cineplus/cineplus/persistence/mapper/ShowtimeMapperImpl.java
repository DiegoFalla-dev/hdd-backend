package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.ShowtimeDto;
import com.cineplus.cineplus.domain.entity.Cinema;
import com.cineplus.cineplus.domain.entity.Movie;
import com.cineplus.cineplus.domain.entity.Showtime;
import com.cineplus.cineplus.domain.entity.Theater;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T00:45:44-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ShowtimeMapperImpl implements ShowtimeMapper {

    @Override
    public ShowtimeDto toDto(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }

        ShowtimeDto showtimeDto = new ShowtimeDto();

        showtimeDto.setMovieId( showtimeMovieId( showtime ) );
        showtimeDto.setMovieTitle( showtimeMovieTitle( showtime ) );
        showtimeDto.setTheaterId( showtimeTheaterId( showtime ) );
        showtimeDto.setTheaterName( showtimeTheaterName( showtime ) );
        showtimeDto.setCinemaId( showtimeTheaterCinemaId( showtime ) );
        showtimeDto.setCinemaName( showtimeTheaterCinemaName( showtime ) );
        showtimeDto.setTotalSeats( showtimeTheaterTotalSeats( showtime ) );
        showtimeDto.setSeatMatrixType( mapSeatMatrixTypeToString( showtimeTheaterSeatMatrixType( showtime ) ) );
        showtimeDto.setAvailableSeats( showtime.getAvailableSeats() );
        showtimeDto.setDate( showtime.getDate() );
        showtimeDto.setFormat( showtime.getFormat() );
        showtimeDto.setId( showtime.getId() );
        showtimeDto.setTime( showtime.getTime() );

        return showtimeDto;
    }

    @Override
    public Showtime toEntity(ShowtimeDto showtimeDto) {
        if ( showtimeDto == null ) {
            return null;
        }

        Showtime showtime = new Showtime();

        showtime.setAvailableSeats( showtimeDto.getAvailableSeats() );
        showtime.setDate( showtimeDto.getDate() );
        showtime.setFormat( showtimeDto.getFormat() );
        showtime.setTime( showtimeDto.getTime() );

        return showtime;
    }

    private Long showtimeMovieId(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }
        Movie movie = showtime.getMovie();
        if ( movie == null ) {
            return null;
        }
        Long id = movie.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String showtimeMovieTitle(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }
        Movie movie = showtime.getMovie();
        if ( movie == null ) {
            return null;
        }
        String title = movie.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }

    private Long showtimeTheaterId(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }
        Theater theater = showtime.getTheater();
        if ( theater == null ) {
            return null;
        }
        Long id = theater.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String showtimeTheaterName(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }
        Theater theater = showtime.getTheater();
        if ( theater == null ) {
            return null;
        }
        String name = theater.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long showtimeTheaterCinemaId(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }
        Theater theater = showtime.getTheater();
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

    private String showtimeTheaterCinemaName(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }
        Theater theater = showtime.getTheater();
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

    private int showtimeTheaterTotalSeats(Showtime showtime) {
        if ( showtime == null ) {
            return 0;
        }
        Theater theater = showtime.getTheater();
        if ( theater == null ) {
            return 0;
        }
        int totalSeats = theater.getTotalSeats();
        return totalSeats;
    }

    private Theater.SeatMatrixType showtimeTheaterSeatMatrixType(Showtime showtime) {
        if ( showtime == null ) {
            return null;
        }
        Theater theater = showtime.getTheater();
        if ( theater == null ) {
            return null;
        }
        Theater.SeatMatrixType seatMatrixType = theater.getSeatMatrixType();
        if ( seatMatrixType == null ) {
            return null;
        }
        return seatMatrixType;
    }
}
