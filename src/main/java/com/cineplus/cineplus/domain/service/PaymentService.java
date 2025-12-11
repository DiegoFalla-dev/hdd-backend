package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.entity.PaymentStatus;
import com.cineplus.cineplus.domain.entity.PaymentTransaction;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentTransaction processSandboxPayment(Long orderId, Long paymentMethodId, BigDecimal amount, String currency, String note);
}
