package com.cineplus.cineplus.persistence.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.cineplus.cineplus.domain.dto.*;
import com.cineplus.cineplus.domain.entity.*;
import com.cineplus.cineplus.domain.repository.*;
import com.cineplus.cineplus.domain.service.*;
import com.cineplus.cineplus.persistence.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private long id;
    private String username;
    private String password;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper; // Asumiendo que UserMapper mapea entre User y UserDto

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

    @Transactional
    public User registerNewUser(RegisterRequestDto registerRequest) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use!");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setNationalId(registerRequest.getNationalId());
        user.setEmail(registerRequest.getEmail());

        if (registerRequest.getBirthDate() != null && !registerRequest.getBirthDate().isBlank()) {
            try {
                user.setBirthDate(registerRequest.getBirthDate());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid birth date format in registration", e);
            }
        }

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
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional // Las actualizaciones suelen requerir una transacción
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        if (userDto.getFirstName() != null && !userDto.getFirstName().isBlank()) {
            existingUser.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null && !userDto.getLastName().isBlank()) {
            existingUser.setLastName(userDto.getLastName());
        }
        // Asumiendo que 'birthDate' en UserDto es String y en User es java.sql.Date
        if (userDto.getBirthDate() != null && !userDto.getBirthDate().isBlank()) {
            try {
                existingUser.setBirthDate(userDto.getBirthDate()); // Convertir String a java.sql.Date
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid birth date format for update", e);
            }
        }
        if (userDto.getGender() != null && !userDto.getGender().isBlank()) {
            existingUser.setGender(userDto.getGender()); // Asegúrate de que el tipo de datos coincida (String o Enum)
        }
        if (userDto.getNationalId() != null && !userDto.getNationalId().isBlank()) {
            // Aquí puedes añadir validación de formato para el DNI
            existingUser.setNationalId(userDto.getNationalId());
        }
        // El avatar podría ser una URL o una cadena base64.
        // Si es base64, guarda la cadena o un identificador de almacenamiento.
        if (userDto.getAvatar() != null) {
            existingUser.setAvatar(userDto.getAvatar());
        }

        // Guarda el usuario actualizado en la base de datos
        User updatedUser = userRepository.save(existingUser);

        // Mapea la entidad actualizada a un DTO y devuélvela
        return userMapper.toDto(updatedUser);
    }
}