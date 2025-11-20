package com.cineplus.cineplus;

import com.cineplus.cineplus.domain.entity.Seat;
import com.cineplus.cineplus.domain.entity.Cinema;
import com.cineplus.cineplus.domain.entity.Theater;
import com.cineplus.cineplus.domain.entity.Movie;
import com.cineplus.cineplus.domain.entity.Showtime;
import com.cineplus.cineplus.domain.entity.MovieStatus;
import com.cineplus.cineplus.domain.repository.SeatRepository;
import com.cineplus.cineplus.domain.repository.CinemaRepository;
import com.cineplus.cineplus.domain.repository.TheaterRepository;
import com.cineplus.cineplus.domain.repository.MovieRepository;
import com.cineplus.cineplus.domain.repository.ShowtimeRepository;
import com.cineplus.cineplus.domain.service.SeatReservationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Disabled;

@Disabled("Integration test requires Docker; enable when Docker available")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeatReservationConcurrencyIT {

    @Container
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withDatabaseName("cineplus_db").withUsername("root").withPassword("root");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mysql.getUsername());
        registry.add("spring.datasource.password", () -> mysql.getPassword());
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    SeatReservationService reservationService;

    @Autowired
    CinemaRepository cinemaRepository;

    @Autowired
    TheaterRepository theaterRepository;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    ShowtimeRepository showtimeRepository;

    @Test
    public void concurrentReserve_sameSeat_onlyOneSucceeds() throws InterruptedException {
        // Prepare minimal cinema/theater/movie/showtime to satisfy non-null constraints
        Cinema cinema = new Cinema();
        cinema.setName("Test Cinema");
        cinemaRepository.save(cinema);

        Theater theater = new Theater();
        theater.setCinema(cinema);
        theater.setName("Sala 1");
        theater.setSeatMatrixType(Theater.SeatMatrixType.SMALL);
        theater.setRowCount(1);
        theater.setColCount(1);
        theater.setTotalSeats(1);
        theaterRepository.save(theater);

        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Drama");
        movie.setClassification("G");
        movie.setDuration("1h");
        movie.setStatus(MovieStatus.CARTELERA);
        movieRepository.save(movie);

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setTheater(theater);
        showtime.setDate(java.time.LocalDate.now());
        showtime.setTime(java.time.LocalTime.now().plusHours(1));
        showtime.setFormat(Showtime.FormatType._2D);
        showtime.setAvailableSeats(1);
        showtimeRepository.save(showtime);

        // Prepare seat
        Seat seat = new Seat();
        seat.setShowtime(showtime);
        seat.setSeatIdentifier("A1");
        seat.setStatus(Seat.SeatStatus.AVAILABLE);
        seatRepository.save(seat);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        Runnable task = () -> {
            ready.countDown();
            try {
                start.await();
                try {
                    reservationService.reserveSeats(100L, Collections.singletonList(seat.getId()), null);
                    successCount.incrementAndGet();
                } catch (Exception ex) {
                    // expected for losers
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();

        // wait both ready
        ready.await();
        // start together
        start.countDown();

        t1.join();
        t2.join();

        Assertions.assertEquals(1, successCount.get(), "Only one reservation should succeed");

        // verify seat state is HELD
        Seat s = seatRepository.findById(seat.getId()).orElseThrow();
        Assertions.assertEquals(Seat.SeatStatus.TEMPORARILY_RESERVED, s.getStatus());
    }
}
