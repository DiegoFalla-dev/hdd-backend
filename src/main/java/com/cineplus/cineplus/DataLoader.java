package com.cineplus.cineplus;

import com.cineplus.cineplus.domain.entity.Role;
import com.cineplus.cineplus.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Aseguramos que los roles existen
        Arrays.stream(Role.RoleName.values())
                .forEach(roleName -> {
                    if (roleRepository.findByName(roleName).isEmpty()) {
                        roleRepository.save(new Role(null, roleName));
                        System.out.println("Role '" + roleName + "' created.");
                    }
                });
    }
}