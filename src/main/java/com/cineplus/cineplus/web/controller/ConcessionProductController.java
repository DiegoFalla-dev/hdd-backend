package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.ConcessionProductDto;
import com.cineplus.cineplus.domain.entity.ConcessionProduct;
import com.cineplus.cineplus.domain.service.ConcessionProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @CrossOrigin removed, now global CORS config is used
@RestController
@RequestMapping("/api/concessions")
@RequiredArgsConstructor
public class ConcessionProductController {

    private final ConcessionProductService  concessionProductService;

    // GET /api/concessions?cinema={id}
    // GET /api/concessions?cinema={id}&category={category}
    @GetMapping
    public ResponseEntity<List<ConcessionProductDto>> getConcessionProducts(
            @RequestParam Long cinema,
            @RequestParam(required = false) ConcessionProduct.ProductCategory category) {

        if (category != null) {
            return ResponseEntity.ok(concessionProductService.findProductsByCinemaIdAndCategory(cinema, category));
        } else {
            return ResponseEntity.ok(concessionProductService.findProductsByCinemaId(cinema));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConcessionProductDto> getProductById(@PathVariable Long id) {
        return concessionProductService.findProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ConcessionProductDto> createProduct(@Valid @RequestBody ConcessionProductDto productDto) {
        ConcessionProductDto createdProduct = concessionProductService.saveProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ConcessionProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ConcessionProductDto productDto) {
        productDto.setId(id);
        return concessionProductService.findProductById(id)
                .map(existingProduct -> ResponseEntity.ok(concessionProductService.saveProduct(productDto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (concessionProductService.findProductById(id).isPresent()) {
            concessionProductService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}