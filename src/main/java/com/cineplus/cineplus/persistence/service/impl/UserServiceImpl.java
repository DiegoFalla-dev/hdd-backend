package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.RegisterRequestDto;
import com.cineplus.cineplus.domain.entity.Role;
import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.repository.RoleRepository;
import com.cineplus.cineplus.domain.repository.UserRepository;
import com.cineplus.cineplus.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User registerNewUser(RegisterRequestDto registerRequest) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use!");
        }

        User user = new User();
        // Use provided first/last names
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
    user.setNationalId(registerRequest.getNationalId());
        user.setEmail(registerRequest.getEmail());
        user.setBirthDate(registerRequest.getBirthDate());
        user.setAvatar(registerRequest.getAvatar());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // phone stored encrypted
        if (registerRequest.getPhone() != null) {
            user.setPhoneEncrypted(com.cineplus.cineplus.persistence.util.Encryptor.encrypt(registerRequest.getPhone()));
        }

        // Asignar el rol por defecto (ej. ROLE_USER)
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User Role not found."));
        user.setRoles(Collections.singleton(userRole)); // Assign default ROLE_USER

        return userRepository.save(user);
    }
}