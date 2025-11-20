package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.ConcessionProductDto;
import com.cineplus.cineplus.domain.entity.ConcessionProduct;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-20T00:45:44-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ConcessionProductMapperImpl implements ConcessionProductMapper {

    @Override
    public ConcessionProductDto toDto(ConcessionProduct concessionProduct) {
        if ( concessionProduct == null ) {
            return null;
        }

        ConcessionProductDto concessionProductDto = new ConcessionProductDto();

        concessionProductDto.setCinemaId( cinemasToCinemaId( concessionProduct.getCinemas() ) );
        concessionProductDto.setCategory( concessionProduct.getCategory() );
        concessionProductDto.setDescription( concessionProduct.getDescription() );
        concessionProductDto.setId( concessionProduct.getId() );
        concessionProductDto.setImageUrl( concessionProduct.getImageUrl() );
        concessionProductDto.setName( concessionProduct.getName() );
        concessionProductDto.setPrice( concessionProduct.getPrice() );

        return concessionProductDto;
    }

    @Override
    public ConcessionProduct toEntity(ConcessionProductDto concessionProductDto) {
        if ( concessionProductDto == null ) {
            return null;
        }

        ConcessionProduct concessionProduct = new ConcessionProduct();

        concessionProduct.setCategory( concessionProductDto.getCategory() );
        concessionProduct.setDescription( concessionProductDto.getDescription() );
        concessionProduct.setImageUrl( concessionProductDto.getImageUrl() );
        concessionProduct.setName( concessionProductDto.getName() );
        concessionProduct.setPrice( concessionProductDto.getPrice() );

        return concessionProduct;
    }
}
