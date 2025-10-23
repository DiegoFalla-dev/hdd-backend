package com.cineplus.cineplus.domain.service;

import com.cineplus.cineplus.domain.dto.RegisterRequestDto;
import com.cineplus.cineplus.domain.dto.UserDto;
import com.cineplus.cineplus.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User registerNewUser(RegisterRequestDto registerRequest);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto userDto);
}