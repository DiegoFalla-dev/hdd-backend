package com.cineplus.cineplus.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "halls")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hall {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String seatMatrixType; // small/medium/large/xlarge
    private Integer capacity;
    @ManyToOne(fetch = FetchType.LAZY)
    private Cinema cinema;
}