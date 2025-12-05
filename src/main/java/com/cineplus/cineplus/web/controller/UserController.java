package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.NameDto;
import com.cineplus.cineplus.domain.dto.OrderDTO;
import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.service.OrderService;
import com.cineplus.cineplus.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

