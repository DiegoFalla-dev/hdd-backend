package com.cineplus.cineplus.web.security;

import com.cineplus.cineplus.domain.entity.Order;
import com.cineplus.cineplus.domain.entity.OrderItem;
import com.cineplus.cineplus.domain.entity.User; // Importar la entidad User
import com.cineplus.cineplus.domain.repository.OrderItemRepository;
import com.cineplus.cineplus.domain.repository.OrderRepository;
import com.cineplus.cineplus.domain.repository.UserRepository; // Importar UserRepository
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails; // Importar UserDetails
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("userSecurity") // El nombre "userSecurity" es el que se usa en @PreAuthorize
@RequiredArgsConstructor
public class UserSecurity {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository; // Inyectar UserRepository

    /**
     * Verifica si el usuario autenticado es el propietario de la orden con el ID dado.
     */
    public boolean hasOrderId(Authentication authentication, Long orderId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return false;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return false;
        }

        // Asumo que la entidad Order tiene una relación @ManyToOne con User
        // y que la entidad User tiene un método getEmail()
        Order order = orderOptional.get();
        // Asegúrate de que order.getUser() no sea null antes de llamar a getEmail()
        return order.getUser() != null && order.getUser().getEmail().equals(username);
    }

    /**
     * Verifica si el usuario autenticado es el propietario del item de orden (ticket) con el ID dado.
     */
    public boolean hasOrderItemId(Authentication authentication, Long orderItemId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return false;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItemId);
        if (orderItemOptional.isEmpty()) {
            return false;
        }

        OrderItem orderItem = orderItemOptional.get();
        // Asegúrate de que orderItem.getOrder() y orderItem.getOrder().getUser() no sean null
        return orderItem.getOrder() != null &&
                orderItem.getOrder().getUser() != null &&
                orderItem.getOrder().getUser().getEmail().equals(username);
    }

    /**
     * Verifica si el usuario autenticado coincide con el ID de usuario proporcionado.
     * Útil para endpoints como GET /api/orders/user/{userId}
     */
    public boolean hasUserId(Authentication authentication, Long userId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return false;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername(); // Obtiene el email del usuario autenticado

        // Busca el usuario por email y compara su ID
        Optional<User> userOptional = userRepository.findByEmail(username);
        return userOptional.map(user -> user.getId().equals(userId)).orElse(false);
    }
}