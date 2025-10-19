package com.cineplus.cineplus.persistance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userExternalId;
    private OffsetDateTime createdAt;
    private Integer totalCents;
    @Enumerated(EnumType.STRING)
    private BookingStatus status; // PENDING, PAID, CANCELLED
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeats;
    public enum BookingStatus { PENDING, PAID, CANCELLED }
}