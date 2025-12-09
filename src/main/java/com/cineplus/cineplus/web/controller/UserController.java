package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.NameDto;
import com.cineplus.cineplus.domain.dto.OrderDTO;
import com.cineplus.cineplus.domain.dto.UserDto;
import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.service.OrderService;
import com.cineplus.cineplus.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.findUserDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/name")
    public ResponseEntity<NameDto> getUserName(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> new NameDto(user.getFirstName(), user.getLastName()))
                .map(nameDto -> ResponseEntity.ok(nameDto))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/purchases")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDTO>> getUserPurchases(@PathVariable Long id) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByUserId(id);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error retrieving purchases for user {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userService.countUsers());
    }
}

