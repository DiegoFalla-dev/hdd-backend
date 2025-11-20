package com.cineplus.cineplus;

import com.cineplus.cineplus.domain.entity.*;
import com.cineplus.cineplus.domain.repository.*;
import com.cineplus.cineplus.domain.service.SeatReservationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "seats.ttl.seconds=5",
        "seats.expiration.poll-ms=1000"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SeatReservationH2Tests {

    @TestConfiguration
    static class MvcTestConfig {
        @Bean
        public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
            return new HandlerMappingIntrospector();
        }
    }

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    SeatReservationRepository reservationRepository;

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

    @Autowired
    com.cineplus.cineplus.domain.repository.OrderRepository orderRepository;

    private Showtime setupShowtimeOneSeat() {
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
        showtime.setDate(LocalDate.now());
        showtime.setTime(LocalTime.now().plusHours(1));
        showtime.setFormat(Showtime.FormatType._2D);
        showtime.setAvailableSeats(1);
        showtimeRepository.save(showtime);

        Seat seat = new Seat();
        seat.setShowtime(showtime);
        seat.setSeatIdentifier("A1");
        seat.setStatus(Seat.SeatStatus.AVAILABLE);
        seatRepository.save(seat);

        return showtime;
    }

    @BeforeEach
    public void beforeEach() {
        reservationRepository.deleteAll();
        seatRepository.deleteAll();
        showtimeRepository.deleteAll();
        movieRepository.deleteAll();
        theaterRepository.deleteAll();
        cinemaRepository.deleteAll();
    }

    @Test
    public void concurrency_twoThreads_onlyOneSucceeds() throws Exception {
        Showtime showtime = setupShowtimeOneSeat();
        Seat seat = seatRepository.findByShowtimeId(showtime.getId()).get(0);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        Runnable task = () -> {
            ready.countDown();
            try {
                start.await();
                try {
                    reservationService.reserveSeats(showtime.getId(), Collections.singletonList(seat.getId()), null);
                    successCount.incrementAndGet();
                } catch (Exception ex) {
                    // expected for one thread
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();

        ready.await();
        start.countDown();
        t1.join();
        t2.join();

        Assertions.assertEquals(1, successCount.get());
        Seat s = seatRepository.findById(seat.getId()).orElseThrow();
        Assertions.assertEquals(Seat.SeatStatus.TEMPORARILY_RESERVED, s.getStatus());
    }

    @Test
    public void expiration_releaseExpiredReservations() throws Exception {
        Showtime showtime = setupShowtimeOneSeat();
        Seat seat = seatRepository.findByShowtimeId(showtime.getId()).get(0);

        // Reserve
        var resp = reservationService.reserveSeats(showtime.getId(), Collections.singletonList(seat.getId()), null);

        // Set reservation(s) to expired
        List<SeatReservation> rs = reservationRepository.findAllByToken(resp.getToken());
        Assertions.assertFalse(rs.isEmpty());
        for (SeatReservation r : rs) {
            r.setExpiresAt(LocalDateTime.now().minusSeconds(10));
        }
        reservationRepository.saveAll(rs);

        // Call release job manually
        reservationService.releaseExpiredReservations();

        Seat s = seatRepository.findById(seat.getId()).orElseThrow();
        Assertions.assertEquals(Seat.SeatStatus.AVAILABLE, s.getStatus());

        for (SeatReservation r : rs) {
            SeatReservation fresh = reservationRepository.findById(r.getId()).orElseThrow();
            Assertions.assertEquals(ReservationStatus.RELEASED, fresh.getStatus());
        }
    }

    @Test
    public void confirmReservation_createsOrder_andOccupiesSeats() throws Exception {
        Showtime showtime = setupShowtimeOneSeat();
        Seat seat = seatRepository.findByShowtimeId(showtime.getId()).get(0);

        var resp = reservationService.reserveSeats(showtime.getId(), Collections.singletonList(seat.getId()), 123L);

        Long orderId = reservationService.confirmReservation(showtime.getId(), resp.getReservationId(), resp.getToken());

        Assertions.assertNotNull(orderId);

        var orderOpt = orderRepository.findByIdWithItems(orderId);
        Assertions.assertTrue(orderOpt.isPresent());
        var order = orderOpt.get();
        Assertions.assertEquals(showtime.getId(), order.getShowtimeId());
        Assertions.assertEquals(123L, order.getUserId());
        Assertions.assertFalse(order.getItems().isEmpty());

        Seat s = seatRepository.findById(seat.getId()).orElseThrow();
        Assertions.assertEquals(Seat.SeatStatus.OCCUPIED, s.getStatus());
    }

    @Test
    public void confirmReservation_idempotent() throws Exception {
        Showtime showtime = setupShowtimeOneSeat();
        Seat seat = seatRepository.findByShowtimeId(showtime.getId()).get(0);

        var resp = reservationService.reserveSeats(showtime.getId(), Collections.singletonList(seat.getId()), 456L);

        Long order1 = reservationService.confirmReservation(showtime.getId(), resp.getReservationId(), resp.getToken());
        Long order2 = reservationService.confirmReservation(showtime.getId(), resp.getReservationId(), resp.getToken());

        Assertions.assertNotNull(order1);
        Assertions.assertEquals(order1, order2);
        Assertions.assertEquals(1L, orderRepository.count());
    }
}
