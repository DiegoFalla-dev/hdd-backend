package com.cineplus.cineplus.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cinemas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cinema {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String externalId;
    private String name;
    private String city;
    private String address;
}