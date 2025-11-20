package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    /**
     * Obtener todos los items de una compra específica
     */
    List<PurchaseItem> findByPurchaseId(Long purchaseId);

    /**
     * Obtener items de tipo específico para una compra
     */
    @Query("SELECT pi FROM PurchaseItem pi WHERE pi.purchase.id = :purchaseId " +
           "AND pi.itemType = :itemType")
    List<PurchaseItem> findByPurchaseIdAndItemType(
        @Param("purchaseId") Long purchaseId,
        @Param("itemType") String itemType
    );
}
