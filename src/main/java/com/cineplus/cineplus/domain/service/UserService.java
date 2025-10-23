package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.RegisterRequestDto;
import com.cineplus.cineplus.domain.dto.UserDto; // Necesitaremos un UserDto simple
import com.cineplus.cineplus.domain.entity.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User registerNewUser(RegisterRequestDto registerRequest);
    // Métodos para actualizar/eliminar usuario si fueran necesarios
}