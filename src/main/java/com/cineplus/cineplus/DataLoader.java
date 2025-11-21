package com.cineplus.cineplus;

import com.cineplus.cineplus.domain.entity.Role;
import com.cineplus.cineplus.domain.entity.Role.RoleName;
import com.cineplus.cineplus.domain.repository.RoleRepository;
import com.cineplus.cineplus.domain.repository.ShowtimeRepository;
import com.cineplus.cineplus.domain.repository.TicketTypeRepository;
import com.cineplus.cineplus.domain.entity.TicketType;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final ShowtimeRepository showtimeRepository;
    private final TicketTypeRepository ticketTypeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Asegúrate de que los roles existan
        if (roleRepository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ROLE_ADMIN));
        }
        if (roleRepository.findByName(RoleName.ROLE_MANAGER).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ROLE_MANAGER));
        }
        if (roleRepository.findByName(RoleName.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(null, RoleName.ROLE_USER));
        }



        // Seed default ticket types if none exist
        try {
            if (ticketTypeRepository.count() == 0) {
                ticketTypeRepository.saveAll(java.util.List.of(
                        TicketType.builder().code("PROMO_ONLINE").name("PROMO ONLINE").price(BigDecimal.valueOf(14.96)).active(true).build(),
                        TicketType.builder().code("DISABLED").name("PERSONA CON DISCAPACIDAD").price(BigDecimal.valueOf(17.70)).active(true).build(),
                        TicketType.builder().code("WHEELCHAIR").name("SILLA DE RUEDAS").price(BigDecimal.valueOf(17.70)).active(true).build(),
                        TicketType.builder().code("CHILD").name("NIÑO").price(BigDecimal.valueOf(21.60)).active(true).build(),
                        TicketType.builder().code("ADULT").name("ADULTO").price(BigDecimal.valueOf(23.60)).active(true).build(),
                        TicketType.builder().code("CONV_RIPLEY").name("50% DCTO BANCO RIPLEY").price(BigDecimal.valueOf(12.80)).active(true).build()
                ));
            }
        } catch (Exception ex) {
            System.err.println("DataLoader: error seeding ticket types: " + ex.getMessage());
        }
    }
}