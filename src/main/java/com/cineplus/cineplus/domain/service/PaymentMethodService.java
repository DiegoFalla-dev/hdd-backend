package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.entity.PaymentMethod;

import java.util.List;

public interface PaymentMethodService {
    PaymentMethod addPaymentMethod(Long userId, PaymentMethod paymentMethod);
    List<PaymentMethod> getPaymentMethodsForUser(Long userId);
}
