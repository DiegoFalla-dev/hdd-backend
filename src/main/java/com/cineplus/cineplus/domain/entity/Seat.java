package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(nullable = false, length = 5)
    private String seatIdentifier; // e.g., "A1", "B10"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    public enum SeatStatus {
        AVAILABLE, OCCUPIED, TEMPORARILY_RESERVED
    }
}