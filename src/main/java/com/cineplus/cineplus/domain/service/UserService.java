package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.RegisterRequestDto;
import com.cineplus.cineplus.domain.dto.UserDto;
import com.cineplus.cineplus.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User registerNewUser(RegisterRequestDto registerRequest);
    
    // Admin management endpoints
    List<UserDto> findAllUsers();
    Optional<UserDto> findUserDtoById(Long id);
    UserDto createUser(UserDto userDto);
    Optional<UserDto> updateUser(Long id, UserDto userDto);
    boolean deleteUser(Long id);
    long countUsers();
    
    // Validation
    boolean validateUserAccount(Long id);
}