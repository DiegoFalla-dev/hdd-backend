package com.cineplus.cineplus.config;

import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(new BCryptPasswordEncoder().encode("admin"));
            admin.setRoles(Set.of("ROLE_ADMIN"));
            userRepository.save(admin);
        }
    }
}
