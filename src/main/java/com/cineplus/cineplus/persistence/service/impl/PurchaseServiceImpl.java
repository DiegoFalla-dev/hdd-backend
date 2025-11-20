package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.CreatePurchaseDto;
import com.cineplus.cineplus.domain.dto.PaymentResponseDto;
import com.cineplus.cineplus.domain.dto.PurchaseDto;
import com.cineplus.cineplus.domain.dto.PurchaseItemRequestDto;
import com.cineplus.cineplus.domain.entity.*;
import com.cineplus.cineplus.domain.repository.*;
import com.cineplus.cineplus.domain.service.PurchaseService;
import com.cineplus.cineplus.domain.service.SeatReservationService;
import com.cineplus.cineplus.persistence.mapper.PurchaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatReservationService seatReservationService;
    private final PurchaseMapper purchaseMapper;

    @Override
    @Transactional
    public PaymentResponseDto processPurchase(CreatePurchaseDto request) {
        log.info("Processing purchase for sessionId: {}", request.getSessionId());

        // 1. Validar que la sesión de reserva existe y está activa
        SeatReservation reservation = seatReservationRepository.findBySessionId(request.getSessionId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, 
                    "Reservation session not found or expired"));

        if (reservation.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(BAD_REQUEST, 
                "Reservation session has expired. Please reserve seats again.");
        }

        // 2. Validar que el usuario existe
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        // 3. Validar que el método de pago existe y pertenece al usuario
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment method not found"));

        if (!paymentMethod.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(FORBIDDEN, 
                "Payment method does not belong to this user");
        }

        // 4. Obtener el showtime
        Showtime showtime = reservation.getShowtime();

        // 5. Validar el monto total
        BigDecimal calculatedAmount = calculateTotalAmount(request.getItems());
        if (calculatedAmount.compareTo(request.getAmount()) != 0) {
            log.warn("Amount mismatch. Expected: {}, Received: {}", 
                calculatedAmount, request.getAmount());
            throw new ResponseStatusException(BAD_REQUEST, 
                String.format("Amount mismatch. Expected: %.2f, Received: %.2f", 
                    calculatedAmount, request.getAmount()));
        }

        // 6. Generar purchaseNumber único
        String purchaseNumber = generatePurchaseNumber();

        // 7. Simular procesamiento de pago
        // TODO: Integrar con pasarela de pago real (Niubiz, MercadoPago, Culqi)
        String transactionId = simulatePaymentProcessing(paymentMethod, request.getAmount());

        // 8. Crear la entidad Purchase
        Purchase purchase = new Purchase();
        purchase.setPurchaseNumber(purchaseNumber);
        purchase.setUser(user);
        purchase.setShowtime(showtime);
        purchase.setPaymentMethod(paymentMethod);
        purchase.setTotalAmount(request.getAmount());
        purchase.setPurchaseDate(LocalDateTime.now());
        purchase.setStatus(PurchaseStatus.COMPLETED);
        purchase.setTransactionId(transactionId);
        purchase.setSessionId(request.getSessionId());

        // 9. Crear los items de compra
        for (PurchaseItemRequestDto itemDto : request.getItems()) {
            PurchaseItem item = new PurchaseItem();
            item.setItemType(PurchaseItemType.valueOf(itemDto.getType()));
            item.setDescription(itemDto.getDescription());
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            item.setConcessionProductId(itemDto.getConcessionProductId());
            item.setSeatIdentifiers(itemDto.getSeatIdentifiers());
            item.calculateSubtotal();
            
            purchase.addItem(item);
        }

        // 10. Guardar la compra
        Purchase savedPurchase = purchaseRepository.save(purchase);
        log.info("Purchase saved with ID: {} and number: {}", 
            savedPurchase.getId(), savedPurchase.getPurchaseNumber());

        // 11. Confirmar los asientos como OCCUPIED
        try {
            seatReservationService.confirmReservation(request.getSessionId(), purchaseNumber);
            log.info("Seats confirmed as OCCUPIED for purchaseNumber: {}", purchaseNumber);
        } catch (Exception e) {
            log.error("Error confirming seats: {}", e.getMessage());
            // La compra ya se guardó, pero los asientos no se confirmaron
            // Esto debería manejarse con una transacción distribuida o compensación
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, 
                "Payment processed but seat confirmation failed. Please contact support.");
        }

        // 12. Retornar respuesta exitosa
        return new PaymentResponseDto(
            true,
            purchaseNumber,
            transactionId,
            "Payment processed successfully"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseDto> getUserPurchases(Long userId) {
        log.info("Fetching purchases for user: {}", userId);
        
        List<Purchase> purchases = purchaseRepository.findByUserIdOrderByPurchaseDateDesc(userId);
        
        return purchases.stream()
                .map(purchaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseDto> getPurchaseByNumber(String purchaseNumber) {
        log.info("Fetching purchase details for: {}", purchaseNumber);
        
        return purchaseRepository.findByPurchaseNumber(purchaseNumber)
                .map(purchaseMapper::toDto);
    }

    @Override
    public String generatePurchaseNumber() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
        
        String purchaseNumber = String.format("CIN-%s-%s", timestamp, random);
        
        // Verificar que no exista (muy improbable, pero por seguridad)
        if (purchaseRepository.existsByPurchaseNumber(purchaseNumber)) {
            return generatePurchaseNumber(); // Recursión para generar otro
        }
        
        return purchaseNumber;
    }

    /**
     * Calcula el monto total basado en los items
     */
    private BigDecimal calculateTotalAmount(List<PurchaseItemRequestDto> items) {
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Simula el procesamiento de pago
     * TODO: Reemplazar con integración real a pasarela de pago
     */
    private String simulatePaymentProcessing(PaymentMethod paymentMethod, BigDecimal amount) {
        // En producción, aquí se llamaría a la API de Niubiz, MercadoPago, etc.
        log.info("Simulating payment processing for amount: {} with payment method ID: {}", 
            amount, paymentMethod.getId());
        
        // Simular delay de procesamiento
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Generar transactionId simulado
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        log.info("Payment simulation successful. TransactionId: {}", transactionId);
        return transactionId;
    }
}
