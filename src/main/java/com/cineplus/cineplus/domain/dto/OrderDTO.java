package com.cineplus.cineplus.domain.dto;

import com.cineplus.cineplus.domain.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private UserDto user; // Podrías usar un UserSimpleDTO si solo necesitas el ID y nombre
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private PaymentMethodDto paymentMethod; // O PaymentMethodSimpleDTO
    private OrderStatus orderStatus;
    private String invoiceNumber;
    private String invoicePdfUrl;
    private String qrCodeUrl;
    private List<OrderItemDTO> orderItems;
    private PromotionDTO promotion; // Si aplica un descuento a la orden
}