package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.TicketTypeDto;
import com.cineplus.cineplus.domain.repository.TicketTypeRepository;
import com.cineplus.cineplus.domain.service.TicketTypeService;
import com.cineplus.cineplus.persistence.mapper.TicketTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;

    @Override
    public List<TicketTypeDto> findAll() {
        return ticketTypeMapper.toDtoList(ticketTypeRepository.findAll());
    }

    @Override
    public Optional<TicketTypeDto> findByCode(String code) {
        return ticketTypeRepository.findByCodeIgnoreCase(code).map(ticketTypeMapper::toDto);
    }
}
