package com.cineplus.cineplus;

import com.cineplus.cineplus.domain.entity.Role;
import com.cineplus.cineplus.domain.entity.Role.RoleName;
import com.cineplus.cineplus.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

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
    }
}