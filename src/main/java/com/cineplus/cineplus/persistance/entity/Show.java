package com.cineplus.cineplus.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "shows")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Show {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne private Movie movie;
    @ManyToOne private Hall hall;
    private OffsetDateTime startTime; // timezone aware
    private String format; // 2D/3D/XD
    private Integer basePriceCents;
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;
}