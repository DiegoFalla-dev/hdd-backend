package com.cineplus.cineplus.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"show_id","row_label","seat_number"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Show show;
    private String rowLabel; // A,B,C...
    private Integer seatNumber;
    @Enumerated(EnumType.STRING)
    private SeatType type;
    @Enumerated(EnumType.STRING)
    private SeatStatus status; // AVAILABLE / BOOKED
    @Version
    private Integer version; // optimistic locking
    public enum SeatType { NORMAL, VIP }
    public enum SeatStatus { AVAILABLE, BOOKED }
}