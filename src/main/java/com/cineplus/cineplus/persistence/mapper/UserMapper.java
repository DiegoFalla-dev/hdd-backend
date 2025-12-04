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
        dto.setType(pm.getType());
        dto.setIsDefault(pm.getIsDefault());
        
        if ("CARD".equals(pm.getType())) {
            // Desencriptar y mostrar últimos 4 dígitos
            String decrypted = null;
            try {
                decrypted = com.cineplus.cineplus.persistence.util.Encryptor.decrypt(pm.getCardNumberEncrypted());
            } catch (Exception ignored) {}
            
            if (decrypted != null && decrypted.length() >= 4) {
                String last4 = decrypted.substring(decrypted.length() - 4);
                dto.setLast4(last4);
                dto.setMaskedCardNumber("**** **** **** " + last4);
            }
            
            // Desencriptar holder name
            try {
                String holderDecrypted = com.cineplus.cineplus.persistence.util.Encryptor.decrypt(pm.getCardHolderEncrypted());
                dto.setHolderName(holderDecrypted);
            } catch (Exception ignored) {}
            
            // Desencriptar y extraer mes y año de expiry
            try {
                String expiryDecrypted = com.cineplus.cineplus.persistence.util.Encryptor.decrypt(pm.getExpiryEncrypted());
                if (expiryDecrypted != null && expiryDecrypted.contains("/")) {
                    String[] parts = expiryDecrypted.split("/");
                    if (parts.length == 2) {
                        dto.setExpMonth(Integer.parseInt(parts[0]));
                        dto.setExpYear(Integer.parseInt(parts[1]));
                    }
                }
            } catch (Exception ignored) {}
        } else if ("YAPE".equals(pm.getType())) {
            // Desencriptar y mostrar últimos 3 dígitos
            String phoneDecrypted = null;
            try {
                phoneDecrypted = com.cineplus.cineplus.persistence.util.Encryptor.decrypt(pm.getPhoneEncrypted());
            } catch (Exception ignored) {}
            
            if (phoneDecrypted != null && phoneDecrypted.length() >= 3) {
                String last3 = phoneDecrypted.substring(phoneDecrypted.length() - 3);
                dto.setPhone(phoneDecrypted); // O solo guardar los últimos 3 si prefieres más privacidad
                dto.setLast4(last3); // Reutilizar este campo para últimos 3 de teléfono
            }
        }
        
        return dto;
    }
}