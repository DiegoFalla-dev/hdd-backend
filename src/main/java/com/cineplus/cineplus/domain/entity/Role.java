package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @Enumerated(EnumType.STRING) // Almacena el nombre del enum como String en la DB
    private RoleName name;

    public enum RoleName {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_MANAGER // Puedes añadir más roles según tus necesidades
    }
}