package com.cineplus.cineplus.domain.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "showtime_seats",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_showtime_seat", columnNames = {"movie_showtime_id", "seat_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_showtime_id", nullable = false, foreignKey = @ForeignKey(name = "fk_showtime_seat_showtime"))
    private Showtime movieShowtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, foreignKey = @ForeignKey(name = "fk_showtime_seat_seat"))
    private Seat seat;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
}
