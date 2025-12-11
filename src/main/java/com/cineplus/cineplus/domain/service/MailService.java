package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.entity.Order;

public interface MailService {
    void sendOrderConfirmation(Order order);
    void sendPasswordResetEmail(String userEmail, String userName, String resetToken);
    void sendWelcomeEmail(String userEmail, String userName);
}
