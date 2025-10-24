package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.OrderStatus;
import com.cineplus.cineplus.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Buscar todas las órdenes de un usuario específico
    List<Order> findByUser(User user);

    // Buscar órdenes por usuario y estado
    List<Order> findByUserAndOrderStatus(User user, OrderStatus orderStatus);

    // Buscar órdenes por estado
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    // Buscar una orden por su número de factura
    Optional<Order> findByInvoiceNumber(String invoiceNumber);

    // Buscar órdenes dentro de un rango de fechas
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}