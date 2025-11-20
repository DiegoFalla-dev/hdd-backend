package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.PromotionDTO;
import com.cineplus.cineplus.domain.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Indica que MapStruct debe generar un componente Spring
public interface PromotionMapper {

    PromotionMapper INSTANCE = Mappers.getMapper(PromotionMapper.class);

    @Mapping(target = "id", source = "id")
    PromotionDTO toDto(Promotion promotion);

    @Mapping(target = "id", source = "id")
    Promotion toEntity(PromotionDTO promotionDTO);

    List<PromotionDTO> toDtoList(List<Promotion> promotions);
    List<Promotion> toEntityList(List<PromotionDTO> promotionDTOs);
}