package com.cineplus.cineplus.domain.service.impl;

import com.cineplus.cineplus.domain.dto.TicketPriceDto;
import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.TicketPrice;
import com.cineplus.cineplus.domain.repository.TicketPriceRepository;
import com.cineplus.cineplus.persistence.mapper.TicketPriceMapper;
import com.cineplus.cineplus.domain.service.TicketPriceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketPriceServiceImpl implements TicketPriceService {

    private final TicketPriceRepository ticketPriceRepository;
    private final TicketPriceMapper mapper;
    @PersistenceContext
    private EntityManager em;

    public TicketPriceServiceImpl(TicketPriceRepository ticketPriceRepository, TicketPriceMapper mapper) {
        this.ticketPriceRepository = ticketPriceRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TicketPriceDto> findByOrderId(Long orderId) {
        return ticketPriceRepository.findByOrderId(orderId).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TicketPriceDto> saveForOrder(Long orderId, List<TicketPriceDto> items) {
        // Obtain a reference to the Order entity (lazy) and map items
        Order order = em.getReference(Order.class, orderId);
        List<TicketPrice> entities = items.stream().map(dto -> mapper.toEntity(dto, order)).collect(Collectors.toList());
        List<TicketPrice> saved = ticketPriceRepository.saveAll(entities);
        return saved.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<BigDecimal> sumSubtotalByOrderId(Long orderId) {
        List<TicketPrice> list = ticketPriceRepository.findByOrderId(orderId);
        if (list == null || list.isEmpty()) return Optional.empty();
        BigDecimal sum = list.stream().map(TicketPrice::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Optional.of(sum);
    }
}
