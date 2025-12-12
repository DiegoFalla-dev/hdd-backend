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

// @CrossOrigin removed, now global CORS config is used
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
    @PreAuthorize("isAuthenticated()")
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

    /**
     * Obtiene los puntos de fidelización del usuario actual o específico
     * @param id ID del usuario
     * @return Objeto con puntos y última compra
     */
    @GetMapping("/{id}/fidelity-points")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFidelityPoints(@PathVariable Long id) {
        try {
            return userService.findById(id)
                    .map(user -> {
                        Integer points = user.getFidelityPoints();
                        return ResponseEntity.ok(java.util.Map.of(
                                "fidelityPoints", points != null ? points : 0,
                                "lastPurchaseDate", user.getLastPurchaseDate() != null ? user.getLastPurchaseDate() : ""
                        ));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error obteniendo puntos de fidelización para usuario {}", id, e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of(
                    "error", "Error obteniendo puntos de fidelización",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Canjea puntos de fidelización por un descuento
     * Descuento: 100 puntos = S/ 10 de descuento
     * @param id ID del usuario
     * @param pointsToRedeem Cantidad de puntos a canjear
     * @return Objeto con resultado del canje
     */
    @PostMapping("/{id}/redeem-points")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> redeemPoints(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> request) {
        try {
            Integer pointsToRedeem = request.get("points");
            if (pointsToRedeem == null || pointsToRedeem <= 0) {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                        "success", false,
                        "message", "Cantidad de puntos inválida"
                ));
            }

            return userService.findById(id)
                    .map(user -> {
                        if (user.getFidelityPoints() < pointsToRedeem) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of(
                                    "success", false,
                                    "message", "Puntos insuficientes para canjear",
                                    "availablePoints", user.getFidelityPoints()
                            ));
                        }

                        // Calcular descuento: 100 puntos = S/ 10
                        double discountAmount = (pointsToRedeem / 100.0) * 10.0;
                        
                        // Restar los puntos
                        user.setFidelityPoints(user.getFidelityPoints() - pointsToRedeem);
                        userService.updateUser(id, new UserDto(user)).orElse(new UserDto());

                        log.info("Usuario {} canjeó {} puntos por S/ {} de descuento", 
                                id, pointsToRedeem, String.format("%.2f", discountAmount));

                        return ResponseEntity.ok(java.util.Map.of(
                                "success", true,
                                "message", "Puntos canjeados exitosamente",
                                "pointsRedeemed", pointsToRedeem,
                                "discountAmount", String.format("%.2f", discountAmount),
                                "remainingPoints", user.getFidelityPoints()
                        ));
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            log.error("Error canjeando puntos para usuario {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of(
                    "success", false,
                    "message", "Error al canjear puntos"
            ));
        }
    }


    /**
     * Actualiza la información de facturación (RUC y Razón Social) del usuario
     * @param id ID del usuario
     * @param billingInfo JSON con ruc y razonSocial
     * @return Respuesta con el usuario actualizado o error
     */
    @PatchMapping("/{id}/billing-info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateBillingInfo(@PathVariable Long id, @RequestBody java.util.Map<String, String> billingInfo) {
        try {
            String ruc = billingInfo.get("ruc");
            String razonSocial = billingInfo.get("razonSocial");
            if (ruc == null || ruc.isBlank() || razonSocial == null || razonSocial.isBlank()) {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                        "success", false,
                        "message", "RUC y Razón Social son obligatorios"
                ));
            }
            return userService.findById(id)
                    .map(user -> {
                        user.setRuc(ruc);
                        user.setRazonSocial(razonSocial);
                        userService.updateUser(id, new UserDto(user));
                        return ResponseEntity.ok(java.util.Map.of(
                                "success", true,
                                "message", "Información de facturación actualizada",
                                "ruc", ruc,
                                "razonSocial", razonSocial
                        ));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error actualizando info de facturación para usuario {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of(
                    "success", false,
                    "message", "Error al actualizar información de facturación"
            ));
        }
    }

    /**
     * Valida la cuenta de un usuario (marca isValid como true)
     * @param id ID del usuario a validar
     * @return Respuesta indicando éxito o error
     */
    @PatchMapping("/{id}/validate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> validateUserAccount(@PathVariable Long id) {
        try {
            boolean validated = userService.validateUserAccount(id);
            if (validated) {
                log.info("Usuario {} validado exitosamente", id);
                return ResponseEntity.ok(java.util.Map.of(
                        "success", true,
                        "message", "Cuenta de usuario validada exitosamente"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error validando cuenta de usuario {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of(
                    "success", false,
                    "message", "Error al validar cuenta de usuario",
                    "error", e.getMessage()
            ));
        }
    }
}
