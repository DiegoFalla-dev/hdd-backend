package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.MovieDto;
import com.cineplus.cineplus.domain.entity.Movie;
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
public class MovieMapperImpl implements MovieMapper {

    @Override
    public MovieDto toDto(Movie movie) {
        if ( movie == null ) {
            return null;
        }

        MovieDto movieDto = new MovieDto();

        movieDto.setCardImageUrl( movie.getCardImageUrl() );
        movieDto.setBannerUrl( movie.getBannerUrl() );
        movieDto.setTrailerUrl( movie.getTrailerUrl() );
        List<String> list = movie.getCast();
        if ( list != null ) {
            movieDto.setCast( new ArrayList<String>( list ) );
        }
        List<String> list1 = movie.getShowtimes();
        if ( list1 != null ) {
            movieDto.setShowtimes( new ArrayList<String>( list1 ) );
        }
        movieDto.setStatus( movie.getStatus() );
        movieDto.setClassification( movie.getClassification() );
        movieDto.setDuration( movie.getDuration() );
        movieDto.setGenre( movie.getGenre() );
        movieDto.setId( movie.getId() );
        movieDto.setSynopsis( movie.getSynopsis() );
        movieDto.setTitle( movie.getTitle() );

        return movieDto;
    }

    @Override
    public Movie toEntity(MovieDto movieDto) {
        if ( movieDto == null ) {
            return null;
        }

        Movie movie = new Movie();

        movie.setCardImageUrl( movieDto.getCardImageUrl() );
        movie.setBannerUrl( movieDto.getBannerUrl() );
        movie.setTrailerUrl( movieDto.getTrailerUrl() );
        List<String> list = movieDto.getCast();
        if ( list != null ) {
            movie.setCast( new ArrayList<String>( list ) );
        }
        List<String> list1 = movieDto.getShowtimes();
        if ( list1 != null ) {
            movie.setShowtimes( new ArrayList<String>( list1 ) );
        }
        movie.setStatus( movieDto.getStatus() );
        movie.setClassification( movieDto.getClassification() );
        movie.setDuration( movieDto.getDuration() );
        movie.setGenre( movieDto.getGenre() );
        movie.setSynopsis( movieDto.getSynopsis() );
        movie.setTitle( movieDto.getTitle() );

        return movie;
    }
}
