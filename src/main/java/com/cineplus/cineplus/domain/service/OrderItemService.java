package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.OrderItemDTO;
import com.cineplus.cineplus.domain.entity.TicketStatus;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {

    List<OrderItemDTO> getAllOrderItems();
    Optional<OrderItemDTO> getOrderItemById(Long id);

    /**
     * Actualiza el estado de un item de orden (ticket).
     * @param id El ID del item de orden.
     * @param newStatus El nuevo estado del ticket (ej. USED).
     * @return El OrderItemDTO actualizado.
     */
    Optional<OrderItemDTO> updateOrderItemStatus(Long id, TicketStatus newStatus);

    /**
     * Obtiene todos los items de una orden específica.
     * @param orderId El ID de la orden.
     * @return Una lista de OrderItemDTOs.
     */
    List<OrderItemDTO> getOrderItemsByOrderId(Long orderId);

    /**
     * Marca un ticket como usado.
     * @param orderItemId El ID del item de la orden.
     * @return true si el ticket fue marcado como usado exitosamente, false si ya estaba usado o no existe.
     */
    boolean markTicketAsUsed(Long orderItemId);
}