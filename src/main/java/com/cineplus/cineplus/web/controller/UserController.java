package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.UserDto;
import com.cineplus.cineplus.domain.service.UserService;
import com.cineplus.cineplus.persistence.service.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "https://hdd-frontend.onrender.com"})
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000", "https://hdd-frontend.onrender.com"})
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)") // Ejemplo de seguridad: Admin o el propio usuario
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me") // Nuevo endpoint para obtener el usuario actual
    @PreAuthorize("isAuthenticated()") // Solo usuarios autenticados
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Asume que tu UserDetailsServiceImpl devuelve un objeto que tiene un ID
        // Necesitarás ajustar esto si tu UserDetails no tiene un método getId() directamente
        // Por ejemplo, si tu UserDetails es una clase personalizada como UserDetailsImpl:
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId(); // Asegúrate de castear a tu tipo real de UserDetails
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden ver todos los usuarios
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}") // O @PostMapping si usas POST para actualizaciones
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)") // Esta es la clave
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto)); // Necesitarás un método updateUser en UserService
    }
}
