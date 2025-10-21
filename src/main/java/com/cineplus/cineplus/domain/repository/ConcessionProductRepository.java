package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.ConcessionProduct;
import com.cineplus.cineplus.domain.entity.ConcessionProduct.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcessionProductRepository extends JpaRepository<ConcessionProduct, Long> {
    @Query("SELECT cp FROM ConcessionProduct cp JOIN cp.cinemas c WHERE c.id = :cinemaId")
    List<ConcessionProduct> findByCinemaId(@Param("cinemaId") Long cinemaId);

    @Query("SELECT cp FROM ConcessionProduct cp JOIN cp.cinemas c WHERE c.id = :cinemaId AND cp.category = :category")
    List<ConcessionProduct> findByCinemaIdAndCategory(@Param("cinemaId") Long cinemaId, @Param("category") ProductCategory category);
}