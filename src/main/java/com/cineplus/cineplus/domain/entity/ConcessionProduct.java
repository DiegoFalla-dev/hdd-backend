package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "concession_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConcessionProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cinema_product",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "cinema_id")
    )
    private Set<Cinema> cinemas; // Los cines donde está disponible este producto

    public enum ProductCategory {
        COMBOS, CANCHITA, BEBIDAS, SNACKS
    }
}