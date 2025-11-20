package com.cineplus.cineplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO completo de una compra para respuestas al frontend
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {
    private Long id;
    private String purchaseNumber;
    private Long userId;
    private String userName;
    private String userEmail;
    
    // Información del showtime (si aplica)
    private Long showtimeId;
    private String movieTitle;
    private String cinemaName;
    private String theaterName;
    private String showtimeDate;
    private String showtimeTime;
    private String showtimeFormat;
    
    // Método de pago
    private Long paymentMethodId;
    private String maskedCardNumber;
    
    // Items y montos
    private List<PurchaseItemDto> items;
    private BigDecimal totalAmount;
    
    // Metadata
    private LocalDateTime purchaseDate;
    private String status;
    private String transactionId;
    private String sessionId;
}
