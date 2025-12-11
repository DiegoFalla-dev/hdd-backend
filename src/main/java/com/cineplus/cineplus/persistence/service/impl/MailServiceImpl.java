package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.service.MailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@cineplus.local}")
    private String from;

    @Override
    public void sendOrderConfirmation(Order order) {
        try {
            if (order == null || order.getUser() == null || order.getUser().getEmail() == null) {
                log.warn("No se puede enviar correo: orden o email de usuario nulo");
                return;
            }

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(order.getUser().getEmail());
            msg.setSubject("Confirmación de compra #" + order.getId());
            msg.setText("Gracias por tu compra. Número de orden: " + order.getId() + "\nTotal: " + order.getTotalAmount());

            mailSender.send(msg);
            log.info("Correo de confirmación enviado a {} para orden {}", order.getUser().getEmail(), order.getId());
        } catch (Exception e) {
            // En sandbox, si no hay SMTP configurado, solo loguear
            log.warn("No se pudo enviar correo de confirmación: {}", e.getMessage());
        }
    }
}
