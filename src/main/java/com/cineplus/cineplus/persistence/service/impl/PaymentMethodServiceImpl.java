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

    @Override
    @Transactional
    public boolean deletePaymentMethod(Long userId, Long paymentMethodId) {
        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment method not found"));
        if (!pm.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "Cannot delete payment method of another user");
        }
        paymentMethodRepository.delete(pm);
        return true;
    }

    @Override
    @Transactional
    public PaymentMethod setDefault(Long userId, Long paymentMethodId) {
        List<PaymentMethod> methods = paymentMethodRepository.findByUserId(userId);
        PaymentMethod target = methods.stream().filter(m -> m.getId().equals(paymentMethodId))
                .findFirst().orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment method not found for user"));
        // Clear current defaults
        methods.forEach(m -> {
            if (Boolean.TRUE.equals(m.getIsDefault())) {
                m.setIsDefault(false);
                paymentMethodRepository.save(m);
            }
        });
        target.setIsDefault(true);
        return paymentMethodRepository.save(target);
    }
}
