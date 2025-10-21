package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.ConcessionProductDto;
import com.cineplus.cineplus.domain.entity.ConcessionProduct;
import com.cineplus.cineplus.domain.repository.ConcessionProductRepository;
import com.cineplus.cineplus.domain.service.ConcessionProductService;
import com.cineplus.cineplus.persistence.mapper.ConcessionProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcessionProductServiceImpl implements ConcessionProductService {

    private final ConcessionProductRepository concessionProductRepository;
    private final ConcessionProductMapper concessionProductMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ConcessionProductDto> findAllProducts() {
        return concessionProductRepository.findAll().stream()
                .map(concessionProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConcessionProductDto> findProductsByCinemaId(Long cinemaId) {
        return concessionProductRepository.findByCinemaId(cinemaId).stream()
                .map(concessionProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConcessionProductDto> findProductsByCinemaIdAndCategory(Long cinemaId, ConcessionProduct.ProductCategory category) {
        return concessionProductRepository.findByCinemaIdAndCategory(cinemaId, category).stream()
                .map(concessionProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConcessionProductDto> findProductById(Long id) {
        return concessionProductRepository.findById(id)
                .map(concessionProductMapper::toDto);
    }

    @Override
    @Transactional
    public ConcessionProductDto saveProduct(ConcessionProductDto productDto) {
        ConcessionProduct product = concessionProductMapper.toEntity(productDto);
        ConcessionProduct savedProduct = concessionProductRepository.save(product);
        return concessionProductMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        concessionProductRepository.deleteById(id);
    }
}