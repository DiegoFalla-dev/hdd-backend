package com.cineplus.cineplus.domain.dto;

import com.cineplus.cineplus.domain.entity.ConcessionProduct;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConcessionProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private ConcessionProduct.ProductCategory category;
    private Set<Long> cinemaIds; // IDs de los cines donde está disponible
}