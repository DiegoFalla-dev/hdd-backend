package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.SeatDto;
import com.cineplus.cineplus.domain.entity.Seat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T00:46:38-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class SeatMapperImpl implements SeatMapper {

    @Override
    public SeatDto toDto(Seat seat) {
        if ( seat == null ) {
            return null;
        }

        SeatDto seatDto = new SeatDto();

        seatDto.setLabel( seat.getSeatIdentifier() );
        if ( seat.getStatus() != null ) {
            seatDto.setState( seat.getStatus().name() );
        }
        seatDto.setId( seat.getId() );

        return seatDto;
    }

    @Override
    public List<SeatDto> toDtoList(List<Seat> seats) {
        if ( seats == null ) {
            return null;
        }

        List<SeatDto> list = new ArrayList<SeatDto>( seats.size() );
        for ( Seat seat : seats ) {
            list.add( toDto( seat ) );
        }

        return list;
    }
}
