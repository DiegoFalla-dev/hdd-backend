package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByOrder(Order order);
}
