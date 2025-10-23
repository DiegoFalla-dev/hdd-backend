package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.NameDto;
import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}/name")
    public ResponseEntity<NameDto> getUserName(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> new NameDto(user.getFirstName(), user.getLastName()))
                .map(nameDto -> ResponseEntity.ok(nameDto))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


}

