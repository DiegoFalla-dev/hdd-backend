package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.ConcessionProductDto;
import com.cineplus.cineplus.domain.entity.ConcessionProduct;
import com.cineplus.cineplus.domain.service.ConcessionProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "https://hdd-frontend.onrender.com"})
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
    public ResponseEntity<ConcessionProductDto> createProduct(@RequestBody ConcessionProductDto productDto) {
        ConcessionProductDto createdProduct = concessionProductService.saveProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConcessionProductDto> updateProduct(@PathVariable Long id, @RequestBody ConcessionProductDto productDto) {
        productDto.setId(id);
        return concessionProductService.findProductById(id)
                .map(existingProduct -> ResponseEntity.ok(concessionProductService.saveProduct(productDto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (concessionProductService.findProductById(id).isPresent()) {
            concessionProductService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}