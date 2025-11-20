package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.PromotionDTO;
import com.cineplus.cineplus.domain.entity.Promotion;
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
public class PromotionMapperImpl implements PromotionMapper {

    @Override
    public PromotionDTO toDto(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }

        PromotionDTO.PromotionDTOBuilder promotionDTO = PromotionDTO.builder();

        promotionDTO.id( promotion.getId() );
        promotionDTO.code( promotion.getCode() );
        promotionDTO.currentUses( promotion.getCurrentUses() );
        promotionDTO.description( promotion.getDescription() );
        promotionDTO.discountType( promotion.getDiscountType() );
        promotionDTO.endDate( promotion.getEndDate() );
        promotionDTO.isActive( promotion.getIsActive() );
        promotionDTO.maxUses( promotion.getMaxUses() );
        promotionDTO.minAmount( promotion.getMinAmount() );
        promotionDTO.startDate( promotion.getStartDate() );
        promotionDTO.value( promotion.getValue() );

        return promotionDTO.build();
    }

    @Override
    public Promotion toEntity(PromotionDTO promotionDTO) {
        if ( promotionDTO == null ) {
            return null;
        }

        Promotion.PromotionBuilder promotion = Promotion.builder();

        promotion.id( promotionDTO.getId() );
        promotion.code( promotionDTO.getCode() );
        promotion.currentUses( promotionDTO.getCurrentUses() );
        promotion.description( promotionDTO.getDescription() );
        promotion.discountType( promotionDTO.getDiscountType() );
        promotion.endDate( promotionDTO.getEndDate() );
        promotion.isActive( promotionDTO.getIsActive() );
        promotion.maxUses( promotionDTO.getMaxUses() );
        promotion.minAmount( promotionDTO.getMinAmount() );
        promotion.startDate( promotionDTO.getStartDate() );
        promotion.value( promotionDTO.getValue() );

        return promotion.build();
    }

    @Override
    public List<PromotionDTO> toDtoList(List<Promotion> promotions) {
        if ( promotions == null ) {
            return null;
        }

        List<PromotionDTO> list = new ArrayList<PromotionDTO>( promotions.size() );
        for ( Promotion promotion : promotions ) {
            list.add( toDto( promotion ) );
        }

        return list;
    }

    @Override
    public List<Promotion> toEntityList(List<PromotionDTO> promotionDTOs) {
        if ( promotionDTOs == null ) {
            return null;
        }

        List<Promotion> list = new ArrayList<Promotion>( promotionDTOs.size() );
        for ( PromotionDTO promotionDTO : promotionDTOs ) {
            list.add( toEntity( promotionDTO ) );
        }

        return list;
    }
}
