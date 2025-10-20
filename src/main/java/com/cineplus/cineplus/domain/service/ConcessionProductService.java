package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.ConcessionProductDto;
import com.cineplus.cineplus.domain.entity.ConcessionProduct;

import java.util.List;
import java.util.Optional;

public interface ConcessionProductService {
    List<ConcessionProductDto> findAllProducts();
    List<ConcessionProductDto> findProductsByCinemaId(Long cinemaId);
    List<ConcessionProductDto> findProductsByCinemaIdAndCategory(Long cinemaId, ConcessionProduct.ProductCategory category);
    Optional<ConcessionProductDto> findProductById(Long id);
    ConcessionProductDto saveProduct(ConcessionProductDto productDto);
    void deleteProduct(Long id);
}