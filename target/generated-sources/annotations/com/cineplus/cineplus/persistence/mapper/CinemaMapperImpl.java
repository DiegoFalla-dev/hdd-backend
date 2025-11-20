package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.CinemaDto;
import com.cineplus.cineplus.domain.entity.Cinema;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T00:45:44-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class CinemaMapperImpl implements CinemaMapper {

    @Override
    public CinemaDto toDto(Cinema cinema) {
        if ( cinema == null ) {
            return null;
        }

        CinemaDto cinemaDto = new CinemaDto();

        cinemaDto.setAddress( cinema.getAddress() );
        List<String> list = cinema.getAvailableFormats();
        if ( list != null ) {
            cinemaDto.setAvailableFormats( new ArrayList<String>( list ) );
        }
        cinemaDto.setCity( cinema.getCity() );
        cinemaDto.setId( cinema.getId() );
        cinemaDto.setImage( cinema.getImage() );
        cinemaDto.setLocation( cinema.getLocation() );
        cinemaDto.setName( cinema.getName() );

        return cinemaDto;
    }

    @Override
    public Cinema toEntity(CinemaDto cinemaDto) {
        if ( cinemaDto == null ) {
            return null;
        }

        Cinema cinema = new Cinema();

        cinema.setAddress( cinemaDto.getAddress() );
        List<String> list = cinemaDto.getAvailableFormats();
        if ( list != null ) {
            cinema.setAvailableFormats( new ArrayList<String>( list ) );
        }
        cinema.setCity( cinemaDto.getCity() );
        cinema.setImage( cinemaDto.getImage() );
        cinema.setLocation( cinemaDto.getLocation() );
        cinema.setName( cinemaDto.getName() );

        return cinema;
    }
}
