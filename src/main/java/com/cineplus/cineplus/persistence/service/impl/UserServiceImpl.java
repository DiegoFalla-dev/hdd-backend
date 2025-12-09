package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.RegisterRequestDto;
import com.cineplus.cineplus.domain.dto.UserDto;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findUserDtoById(Long id) {
        return userRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setNationalId(userDto.getNationalId());
        user.setEmail(userDto.getEmail());
        user.setBirthDate(userDto.getBirthDate());
        user.setAvatar(userDto.getAvatar());
        user.setPassword(passwordEncoder.encode("TempPass123!"));

        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            user.setRoles(getRolesFromNames(userDto.getRoles()));
        } else {
            Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User Role not found"));
            user.setRoles(Collections.singleton(userRole));
        }

        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public Optional<UserDto> updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id).map(user -> {
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setNationalId(userDto.getNationalId());
            user.setEmail(userDto.getEmail());
            user.setBirthDate(userDto.getBirthDate());
            user.setAvatar(userDto.getAvatar());

            if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
                user.setRoles(getRolesFromNames(userDto.getRoles()));
            }

            return toDto(userRepository.save(user));
        });
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setNationalId(user.getNationalId());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthDate());
        dto.setAvatar(user.getAvatar());
        
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }

    private Set<Role> getRolesFromNames(Set<String> roleNames) {
        return roleNames.stream()
                .map(roleName -> {
                    try {
                        Role.RoleName roleEnum = Role.RoleName.valueOf(roleName);
                        return roleRepository.findByName(roleEnum)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName));
                    } catch (IllegalArgumentException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + roleName);
                    }
                })
                .collect(Collectors.toSet());
    }
}