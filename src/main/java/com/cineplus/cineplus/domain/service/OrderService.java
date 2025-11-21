package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.CreateOrderDTO;
import com.cineplus.cineplus.domain.dto.OrderDTO;
import com.cineplus.cineplus.domain.entity.OrderStatus;

import java.math.BigDecimal; // Necesario para BigDecimal
import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<OrderDTO> getAllOrders();
    Optional<OrderDTO> getOrderById(Long id);

    /**
     * Crea una nueva orden de compra, incluyendo la validación de asientos, cálculo de total,
     * aplicación de promociones y generación de QR/PDF.
     * @param createOrderDTO El DTO con los datos para crear la orden.
     * @return La OrderDTO de la orden creada.
     */
    OrderDTO createOrder(CreateOrderDTO createOrderDTO);

    /**
     * Actualiza el estado de una orden.
     * @param id El ID de la orden a actualizar.
     * @param newStatus El nuevo estado de la orden.
     * @return La OrderDTO actualizada.
     */
    Optional<OrderDTO> updateOrderStatus(Long id, OrderStatus newStatus);

    /**
     * Obtiene todas las órdenes de un usuario específico.
     * @param userId El ID del usuario.
     * @return Una lista de OrderDTOs.
     */
    List<OrderDTO> getOrdersByUserId(Long userId);

    /**
     * Genera el PDF de la factura para una orden.
     * @param orderId El ID de la orden.
     * @return Un array de bytes del PDF.
     */
    byte[] generateInvoicePdf(Long orderId);

    /**
     * Genera el código QR para un item de la orden.
     * @param orderItemId El ID del item de la orden.
     * @return Un array de bytes de la imagen QR.
     */
    byte[] generateTicketQrCode(Long orderItemId);

    /**
     * Genera el PDF del ticket para un item de la orden.
     * @param orderItemId El ID del item de la orden.
     * @return Un array de bytes del PDF.
     */
    byte[] generateTicketPdf(Long orderItemId);

    // Métodos adicionales si necesitas buscar por otros criterios
    // List<OrderDTO> getOrdersByStatus(OrderStatus status);
}