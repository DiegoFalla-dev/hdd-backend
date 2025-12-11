package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String username;
    
    @Column(name = "national_id", length = 50, unique = true)
    private String nationalId; // DNI - not encrypted per request
    // Personal information
    @Column(nullable = false, length = 100)
    private String firstName; // nombre

    @Column(nullable = false, length = 100)
    private String lastName; // apellido

    @Column(nullable = false, unique = true, length = 255)
    private String email; // correo

    @Column(length = 50)
    private String birthDate; // fechaNacimiento (ISO string)

    @Column(length = 20)
    private String gender; // genero

    @Column(length = 255)
    private String avatar;

    @Column(length = 255)
    private String favoriteCinema;

    // Encrypted private fields
    @Column(name = "phone_encrypted", length = 1024)
    private String phoneEncrypted; // celular (encrypted)

    @Column(length = 1024)
    private String password; // contraseña hashed

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PaymentMethod> paymentMethods = new HashSet<>();

    // Fidelización
    @Column(name = "fidelity_points", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer fidelityPoints = 0; // Puntos acumulados (1 punto cada S/.10 gastados)

    @Column(name = "last_purchase_date")
    private LocalDateTime lastPurchaseDate; // Fecha de última compra

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.fidelityPoints = 0;
    }
}