package com.cineplus.cineplus.domain.repository;

import com.cineplus.cineplus.domain.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    Optional<TicketType> findByCodeIgnoreCase(String code);
}
