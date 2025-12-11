package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.CreateOrderDTO;
import com.cineplus.cineplus.domain.dto.OrderDTO;
import com.cineplus.cineplus.domain.dto.OrderItemDTO;
import com.cineplus.cineplus.domain.entity.OrderStatus;
import com.cineplus.cineplus.domain.entity.TicketStatus;
import com.cineplus.cineplus.domain.service.OrderItemService;
import com.cineplus.cineplus.domain.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final OrderItemService orderItemService; // Para las operaciones específicas de OrderItem

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden ver todas las órdenes
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Solo requiere estar autenticado
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()") // Solo requiere estar autenticado
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Long userId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()") // Cualquier usuario autenticado pueden crear órdenes
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        try {
            OrderDTO createdOrder = orderService.createOrder(createOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (EntityNotFoundException e) {
            log.error("Entity not found while creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Illegal state while creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of("error", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden cambiar el estado de una orden
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus newStatus) {
        return orderService.updateOrderStatus(id, newStatus)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Endpoints para OrderItems (Tickets individuales) ---
    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasOrderId(authentication, #orderId)")
    public ResponseEntity<List<OrderItemDTO>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        try {
            List<OrderItemDTO> items = orderItemService.getOrderItemsByOrderId(orderId);
            return ResponseEntity.ok(items);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasOrderItemId(authentication, #itemId)")
    public ResponseEntity<OrderItemDTO> getOrderItemById(@PathVariable Long itemId) {
        return orderItemService.getOrderItemById(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/items/{itemId}/use")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')") // Empleados o admins pueden marcar tickets como usados
    public ResponseEntity<Void> markTicketAsUsed(@PathVariable Long itemId) {
        boolean success = orderItemService.markTicketAsUsed(itemId);
        return success ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }


    // --- Endpoints para generación de archivos (QR/PDF) ---

    // Factura de la orden
    @GetMapping(value = "/{orderId}/invoice-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasOrderId(authentication, #orderId)")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long orderId) {
        try {
            byte[] pdfBytes = orderService.generateInvoicePdf(orderId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "invoice_" + orderId + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // QR de un ticket individual
    @GetMapping(value = "/items/{itemId}/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or @userSecurity.hasOrderItemId(authentication, #itemId)")
    public ResponseEntity<byte[]> getTicketQrCode(@PathVariable Long itemId) {
        try {
            byte[] qrBytes = orderService.generateTicketQrCode(itemId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "ticket_qr_" + itemId + ".png");
            return ResponseEntity.ok().headers(headers).body(qrBytes);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PDF de un ticket individual
    @GetMapping(value = "/items/{itemId}/ticket-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.hasOrderItemId(authentication, #itemId)")
    public ResponseEntity<byte[]> getTicketPdf(@PathVariable Long itemId) {
        try {
            byte[] pdfBytes = orderService.generateTicketPdf(itemId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "ticket_" + itemId + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}