package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.OrderItemDTO;
import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.OrderItem;
import com.cineplus.cineplus.domain.entity.TicketStatus;
import com.cineplus.cineplus.domain.repository.OrderItemRepository;
import com.cineplus.cineplus.domain.repository.OrderRepository; // Si se necesita para buscar items por Order ID
import com.cineplus.cineplus.domain.service.OrderItemService;
import com.cineplus.cineplus.persistence.mapper.OrderItemMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository; // Para obtener la Order si se necesita

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDTO> getAllOrderItems() {
        return orderItemMapper.toDtoList(orderItemRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItemDTO> getOrderItemById(Long id) {
        return orderItemRepository.findById(id).map(orderItemMapper::toDto);
    }

    @Override
    @Transactional
    public Optional<OrderItemDTO> updateOrderItemStatus(Long id, TicketStatus newStatus) {
        return orderItemRepository.findById(id).map(orderItem -> {
            orderItem.setTicketStatus(newStatus);
            return orderItemMapper.toDto(orderItemRepository.save(orderItem));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada con ID: " + orderId));
        return orderItemMapper.toDtoList(orderItemRepository.findByOrder(order));
    }

    @Override
    @Transactional
    public boolean markTicketAsUsed(Long orderItemId) {
        return orderItemRepository.findById(orderItemId).map(orderItem -> {
            if (orderItem.getTicketStatus() == TicketStatus.VALID) {
                orderItem.setTicketStatus(TicketStatus.USED);
                orderItemRepository.save(orderItem);
                return true;
            }
            return false; // Ya estaba usado o cancelado
        }).orElse(false); // Ticket no encontrado
    }
}