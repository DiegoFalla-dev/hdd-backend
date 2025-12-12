package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Buscar todos los items de una orden específica
    List<OrderItem> findByOrder(Order order);

    // Buscar items de orden por función (showtime)
    List<OrderItem> findByShowtime(Showtime showtime);

    // Buscar items de orden por estado del ticket
    List<OrderItem> findByTicketStatus(TicketStatus ticketStatus);

    // Buscar items de orden de una función específica y con un estado de ticket
    List<OrderItem> findByShowtimeAndTicketStatus(Showtime showtime, TicketStatus ticketStatus);

    // Buscar un item de orden por función, asiento de función y estado del ticket
    Optional<OrderItem> findByShowtimeAndShowtimeSeatAndTicketStatus(Showtime showtime, ShowtimeSeat showtimeSeat, TicketStatus ticketStatus);
}