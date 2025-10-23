package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.PaymentMethodDto;
import com.cineplus.cineplus.domain.dto.UserDto;
import com.cineplus.cineplus.domain.entity.PaymentMethod;
import com.cineplus.cineplus.domain.entity.Role;
import com.cineplus.cineplus.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRolesToStrings")
    @Mapping(source = "paymentMethods", target = "paymentMethods", qualifiedByName = "mapPaymentMethods")
    @Mapping(source = "nationalId", target = "nationalId")
    UserDto toDto(User user);

    // Helpers
    @Named("mapRolesToStrings")
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    @Named("mapPaymentMethods")
    default List<PaymentMethodDto> mapPaymentMethods(Set<PaymentMethod> methods) {
        return methods.stream()
                .map(this::toPaymentMethodDto)
                .collect(Collectors.toList());
    }

    default PaymentMethodDto toPaymentMethodDto(PaymentMethod pm) {
        PaymentMethodDto dto = new PaymentMethodDto();
        dto.setId(pm.getId());
        dto.setIsDefault(pm.getIsDefault());
        // Mask card number: show last 4 digits if possible
        String decrypted = null;
        try {
            decrypted = com.cineplus.cineplus.persistence.util.Encryptor.decrypt(pm.getCardNumberEncrypted());
        } catch (Exception ignored) {}
        if (decrypted != null && decrypted.length() >= 4) {
            String last4 = decrypted.substring(decrypted.length() - 4);
            dto.setMaskedCardNumber("**** **** **** " + last4);
        } else {
            dto.setMaskedCardNumber(null);
        }
        return dto;
    }
}