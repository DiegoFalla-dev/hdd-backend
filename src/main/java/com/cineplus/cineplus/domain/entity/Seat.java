package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, length = 10)
    private String seatIdentifier; // e.g., "A1", "B10"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Version
    private Long version;

    public enum SeatStatus {
        AVAILABLE,
        OCCUPIED,
        TEMPORARILY_RESERVED
    }

    public Seat(Long id, Showtime showtime, String seatIdentifier, SeatStatus status) {
        this.id = id;
        this.showtime = showtime;
        this.seatIdentifier = seatIdentifier;
        this.status = status;
    }
}
