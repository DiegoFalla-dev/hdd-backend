package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.entity.Order;

public interface MailService {
    void sendOrderConfirmation(Order order);
}
