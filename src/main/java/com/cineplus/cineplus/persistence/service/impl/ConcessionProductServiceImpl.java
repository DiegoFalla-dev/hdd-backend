package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.ConcessionProductDto;
import com.cineplus.cineplus.domain.entity.Cinema;
import com.cineplus.cineplus.domain.entity.ConcessionProduct;
import com.cineplus.cineplus.domain.repository.CinemaRepository;
import com.cineplus.cineplus.domain.repository.ConcessionProductRepository;
import com.cineplus.cineplus.domain.service.ConcessionProductService;
import com.cineplus.cineplus.persistence.mapper.ConcessionProductMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcessionProductServiceImpl implements ConcessionProductService {

    private static final Logger log = LoggerFactory.getLogger(ConcessionProductServiceImpl.class);

    private final ConcessionProductRepository concessionProductRepository;
    private final ConcessionProductMapper concessionProductMapper;
    private final CinemaRepository cinemaRepository;

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
        log.info("Attempting to save ConcessionProduct. DTO received: {}", productDto); // Log DTO

        ConcessionProduct product = concessionProductMapper.toEntity(productDto);
        log.info("Mapped to entity (before cinema association): {}", product);

        if (productDto.getCinemaId() != null && !productDto.getCinemaId().isEmpty()) {
            Set<Cinema> cinemas = new HashSet<>();
            for (Long cId : productDto.getCinemaId()) { // Cambié cinemaId a cId para evitar confusión con el campo del DTO
                Optional<Cinema> optionalCinema = cinemaRepository.findById(cId);
                if (optionalCinema.isPresent()) {
                    cinemas.add(optionalCinema.get());
                    log.info("Found and added Cinema with ID: {}", cId);
                } else {
                    log.warn("Cinema with ID: {} not found. Skipping association.", cId);
                }
            }
            product.setCinemas(cinemas);
            log.info("Associated Cinemas with product: {}", cinemas.stream().map(Cinema::getId).collect(Collectors.toSet()));
        } else {
            product.setCinemas(Collections.emptySet());
            log.info("No Cinema IDs provided in DTO or DTO's Cinema ID set is empty.");
        }

        ConcessionProduct savedProduct = concessionProductRepository.save(product);
        log.info("Saved ConcessionProduct entity: {}", savedProduct);

        ConcessionProductDto resultDto = concessionProductMapper.toDto(savedProduct);
        log.info("Mapped saved entity back to DTO for response: {}", resultDto); // Log el DTO de respuesta
        return resultDto;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        concessionProductRepository.deleteById(id);
    }
}