package com.cineplus.cineplus.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.cineplus.cineplus.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String nationalId;
    private String email;
    private String birthDate;
    private String gender;
    private String avatar;
    private String username;
    private String phoneNumber;
    private CinemaDto favoriteCinema;
    private Set<String> roles;
    private List<PaymentMethodDto> paymentMethods;
    
    // Información de fidelización
    private Integer fidelityPoints = 0;
    private LocalDateTime lastPurchaseDate;
    
    // Información de seguridad
    private Boolean isActive = true;
    private Boolean isValid = false;
    private Boolean isTwoFactorEnabled = false;
    private LocalDateTime activationTokenExpiry;
    
    // Información de facturación
    private String ruc;
    private String razonSocial;

    /**
     * Convenience constructor to build the DTO from a User entity.
     * Ensures nullable boolean fields are defaulted to safe values to avoid NPEs.
     */
    public UserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.nationalId = user.getNationalId();
        this.email = user.getEmail();
        this.birthDate = user.getBirthDate();
        this.gender = user.getGender();
        this.avatar = user.getAvatar();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();

        if (user.getFavoriteCinemaEntity() != null) {
            CinemaDto cinemaDto = new CinemaDto();
            cinemaDto.setId(user.getFavoriteCinemaEntity().getId());
            cinemaDto.setName(user.getFavoriteCinemaEntity().getName());
            cinemaDto.setCity(user.getFavoriteCinemaEntity().getCity());
            cinemaDto.setAddress(user.getFavoriteCinemaEntity().getAddress());
            cinemaDto.setLocation(user.getFavoriteCinemaEntity().getLocation());
            cinemaDto.setAvailableFormats(user.getFavoriteCinemaEntity().getAvailableFormats());
            cinemaDto.setImage(user.getFavoriteCinemaEntity().getImage());
            this.favoriteCinema = cinemaDto;
        }

        if (user.getRoles() != null) {
            this.roles = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet());
        }

        this.fidelityPoints = user.getFidelityPoints();
        this.lastPurchaseDate = user.getLastPurchaseDate();

        this.isActive = user.getIsActive() != null ? user.getIsActive() : true;
        this.isValid = user.getIsValid() != null ? user.getIsValid() : false;
        this.isTwoFactorEnabled = user.getIsTwoFactorEnabled() != null ? user.getIsTwoFactorEnabled() : false;
        this.activationTokenExpiry = user.getActivationTokenExpiry();

        this.ruc = user.getRuc();
        this.razonSocial = user.getRazonSocial();
    }
}