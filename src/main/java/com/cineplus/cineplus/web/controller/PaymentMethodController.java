package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.PaymentMethodCreateDto;
import com.cineplus.cineplus.domain.dto.PaymentMethodDto;
import com.cineplus.cineplus.domain.entity.PaymentMethod;
import com.cineplus.cineplus.domain.service.PaymentMethodService;
import com.cineplus.cineplus.persistence.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<PaymentMethodDto>> list(@PathVariable Long userId) {
        List<PaymentMethod> methods = paymentMethodService.getPaymentMethodsForUser(userId);
        List<PaymentMethodDto> dtos = methods.stream().map(m -> userMapper.toPaymentMethodDto(m)).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> add(@PathVariable Long userId, @Valid @RequestBody PaymentMethodCreateDto dto) {
        PaymentMethod pm = new PaymentMethod();
        pm.setType(dto.getType()); // CARD o YAPE
        
        if ("CARD".equals(dto.getType())) {
            pm.setCardNumberEncrypted(dto.getCardNumber());
            pm.setCardHolderEncrypted(dto.getCardHolder());
            pm.setCciEncrypted(dto.getCci());
            pm.setExpiryEncrypted(dto.getExpiry());
            // Generate name from card holder and last 4 digits
            String last4 = dto.getCardNumber().length() >= 4 ? dto.getCardNumber().substring(dto.getCardNumber().length() - 4) : dto.getCardNumber();
            pm.setName("Tarjeta •••• " + last4);
        } else if ("YAPE".equals(dto.getType())) {
            pm.setPhoneEncrypted(dto.getPhone());
            pm.setVerificationCodeEncrypted(dto.getVerificationCode());
            // Generate name from phone
            String last4Phone = dto.getPhone().length() >= 4 ? dto.getPhone().substring(dto.getPhone().length() - 4) : dto.getPhone();
            pm.setName("Yape •••• " + last4Phone);
        }
        
        pm.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);

        PaymentMethod saved = paymentMethodService.addPaymentMethod(userId, pm);
        PaymentMethodDto paymentMethodDto = userMapper.toPaymentMethodDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethodDto);
    }

    @DeleteMapping("/{paymentMethodId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long paymentMethodId) {
        boolean deleted = paymentMethodService.deletePaymentMethod(userId, paymentMethodId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{paymentMethodId}/default")
    public ResponseEntity<PaymentMethodDto> makeDefault(@PathVariable Long userId, @PathVariable Long paymentMethodId) {
        PaymentMethod updated = paymentMethodService.setDefault(userId, paymentMethodId);
        PaymentMethodDto dto = userMapper.toPaymentMethodDto(updated);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{paymentMethodId}")
    public ResponseEntity<PaymentMethodDto> update(@PathVariable Long userId, @PathVariable Long paymentMethodId, @Valid @RequestBody PaymentMethodCreateDto dto) {
        PaymentMethod pm = paymentMethodService.getPaymentMethod(userId, paymentMethodId);
        if (pm == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Actualizar solo los campos permitidos
        if ("CARD".equals(pm.getType()) && dto.getCci() != null) {
            pm.setCciEncrypted(dto.getCci());
        }
        
        if ("YAPE".equals(pm.getType()) && dto.getVerificationCode() != null) {
            pm.setVerificationCodeEncrypted(dto.getVerificationCode());
        }
        
        if (dto.getIsDefault() != null) {
            pm.setIsDefault(dto.getIsDefault());
        }
        
        PaymentMethod saved = paymentMethodService.updatePaymentMethod(pm);
        PaymentMethodDto paymentMethodDto = userMapper.toPaymentMethodDto(saved);
        return ResponseEntity.ok(paymentMethodDto);
    }
}