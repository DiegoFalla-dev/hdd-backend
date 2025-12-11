package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.OrderStatus;
import com.cineplus.cineplus.domain.entity.PaymentStatus;
import com.cineplus.cineplus.domain.entity.PaymentTransaction;
import com.cineplus.cineplus.domain.repository.OrderRepository;
import com.cineplus.cineplus.domain.repository.PaymentMethodRepository;
import com.cineplus.cineplus.domain.repository.PaymentTransactionRepository;
import com.cineplus.cineplus.domain.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final Random random = new Random();

    @Override
    @Transactional
    public PaymentTransaction processSandboxPayment(Long orderId, Long paymentMethodId, BigDecimal amount, String currency, String note) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        if (currency == null || currency.isBlank()) {
            currency = "PEN";
        }

        Order order = null;
        if (orderId != null) {
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada para pago: " + orderId));
        }

        if (paymentMethodId != null) {
            paymentMethodRepository.findById(paymentMethodId)
                    .orElseThrow(() -> new EntityNotFoundException("Método de pago no encontrado: " + paymentMethodId));
        }

        // Regla simple: 90% aprobada, 10% declinada, salvo montos 0 que van a DECLINED
        PaymentStatus status;
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            status = PaymentStatus.DECLINED;
        } else {
            status = random.nextInt(10) == 0 ? PaymentStatus.DECLINED : PaymentStatus.APPROVED;
        }

        PaymentTransaction tx = PaymentTransaction.builder()
                .order(order)
                .amount(amount)
                .currency(currency)
                .provider("SANDBOX")
                .status(status)
                .reference("SBX-" + UUID.randomUUID().toString().substring(0, 8))
                .createdAt(LocalDateTime.now())
                .rawResponse(note)
                .build();

        PaymentTransaction saved = paymentTransactionRepository.save(tx);

        // Actualizar estado de la orden si aplica
        if (order != null) {
            if (status == PaymentStatus.APPROVED) {
                order.setOrderStatus(OrderStatus.COMPLETED);
            } else if (status == PaymentStatus.DECLINED) {
                order.setOrderStatus(OrderStatus.CANCELLED);
            }
            orderRepository.save(order);
        }

        return saved;
    }
}
