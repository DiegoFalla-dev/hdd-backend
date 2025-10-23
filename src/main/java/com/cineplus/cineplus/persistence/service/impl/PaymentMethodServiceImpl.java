package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.entity.PaymentMethod;
import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.repository.PaymentMethodRepository;
import com.cineplus.cineplus.domain.repository.UserRepository;
import com.cineplus.cineplus.domain.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentMethod addPaymentMethod(Long userId, PaymentMethod paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        // Encrypt sensitive fields before saving
        paymentMethod.setCardNumberEncrypted(com.cineplus.cineplus.persistence.util.Encryptor.encrypt(paymentMethod.getCardNumberEncrypted()));
        paymentMethod.setCardHolderEncrypted(com.cineplus.cineplus.persistence.util.Encryptor.encrypt(paymentMethod.getCardHolderEncrypted()));
        paymentMethod.setCciEncrypted(com.cineplus.cineplus.persistence.util.Encryptor.encrypt(paymentMethod.getCciEncrypted()));
        paymentMethod.setExpiryEncrypted(com.cineplus.cineplus.persistence.util.Encryptor.encrypt(paymentMethod.getExpiryEncrypted()));
        paymentMethod.setPhoneEncrypted(com.cineplus.cineplus.persistence.util.Encryptor.encrypt(paymentMethod.getPhoneEncrypted()));

        paymentMethod.setUser(user);
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethod> getPaymentMethodsForUser(Long userId) {
        return paymentMethodRepository.findByUserId(userId);
    }
}
