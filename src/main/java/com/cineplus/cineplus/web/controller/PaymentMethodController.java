package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.dto.PaymentMethodCreateDto;
import com.cineplus.cineplus.domain.dto.PaymentMethodDto;
import com.cineplus.cineplus.domain.entity.PaymentMethod;
import com.cineplus.cineplus.domain.service.PaymentMethodService;
import com.cineplus.cineplus.persistence.mapper.UserMapper;
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
    public ResponseEntity<?> add(@PathVariable Long userId, @RequestBody PaymentMethodCreateDto dto) {
        PaymentMethod pm = new PaymentMethod();
        pm.setCardNumberEncrypted(dto.getCardNumber());
        pm.setCardHolderEncrypted(dto.getCardHolder());
        pm.setCciEncrypted(dto.getCci());
        pm.setExpiryEncrypted(dto.getExpiry());
        pm.setPhoneEncrypted(dto.getPhone());
        pm.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);

        PaymentMethod saved = paymentMethodService.addPaymentMethod(userId, pm);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
    }
}
