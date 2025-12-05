package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.TicketTypeDto;

import java.util.List;
import java.util.Optional;

public interface TicketTypeService {
    List<TicketTypeDto> findAll();
    Optional<TicketTypeDto> findByCode(String code);
}
