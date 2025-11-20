package com.cineplus.cineplus.persistence.mapper;

import com.cineplus.cineplus.domain.dto.PurchaseDto;
import com.cineplus.cineplus.domain.dto.PurchaseItemDto;
import com.cineplus.cineplus.domain.entity.Purchase;
import com.cineplus.cineplus.domain.entity.PurchaseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userName", qualifiedByName = "userToFullName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "showtime.id", target = "showtimeId")
    @Mapping(source = "showtime.movie.title", target = "movieTitle")
    @Mapping(source = "showtime.theater.cinema.name", target = "cinemaName")
    @Mapping(source = "showtime.theater.name", target = "theaterName")
    @Mapping(source = "showtime.date", target = "showtimeDate", qualifiedByName = "dateToString")
    @Mapping(source = "showtime.time", target = "showtimeTime", qualifiedByName = "timeToString")
    @Mapping(source = "showtime.format", target = "showtimeFormat", qualifiedByName = "formatToString")
    @Mapping(source = "paymentMethod.id", target = "paymentMethodId")
    @Mapping(source = "paymentMethod", target = "maskedCardNumber", qualifiedByName = "maskCardNumber")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "items", target = "items")
    PurchaseDto toDto(Purchase purchase);

    @Mapping(source = "itemType", target = "itemType", qualifiedByName = "itemTypeToString")
    PurchaseItemDto toItemDto(PurchaseItem item);

    default List<PurchaseItemDto> toItemDtoList(List<PurchaseItem> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }

    @Named("dateToString")
    default String dateToString(java.time.LocalDate date) {
        return date != null ? date.toString() : null;
    }

    @Named("timeToString")
    default String timeToString(java.time.LocalTime time) {
        return time != null ? time.toString() : null;
    }

    @Named("formatToString")
    default String formatToString(com.cineplus.cineplus.domain.entity.Showtime.FormatType format) {
        return format != null ? format.name() : null;
    }

    @Named("statusToString")
    default String statusToString(com.cineplus.cineplus.domain.entity.PurchaseStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("itemTypeToString")
    default String itemTypeToString(com.cineplus.cineplus.domain.entity.PurchaseItemType itemType) {
        return itemType != null ? itemType.name() : null;
    }

    @Named("userToFullName")
    default String userToFullName(com.cineplus.cineplus.domain.entity.User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
    }

    @Named("maskCardNumber")
    default String maskCardNumber(com.cineplus.cineplus.domain.entity.PaymentMethod paymentMethod) {
        if (paymentMethod == null || paymentMethod.getCardNumberEncrypted() == null) {
            return null;
        }
        // Intentar desencriptar y enmascarar
        try {
            String decrypted = com.cineplus.cineplus.persistence.util.Encryptor
                    .decrypt(paymentMethod.getCardNumberEncrypted());
            if (decrypted != null && decrypted.length() >= 4) {
                return "**** **** **** " + decrypted.substring(decrypted.length() - 4);
            }
        } catch (Exception e) {
            // Si falla la desencriptación, retornar un valor genérico
            return "**** **** **** ****";
        }
        return "**** **** **** ****";
    }
}
