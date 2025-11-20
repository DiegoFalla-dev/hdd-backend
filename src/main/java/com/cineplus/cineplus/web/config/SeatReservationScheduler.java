package com.cineplus.cineplus.web.config;

import com.cineplus.cineplus.domain.service.SeatReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tarea programada para liberar reservas de asientos expiradas.
 * Se ejecuta cada 30 segundos para garantizar liberación oportuna.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeatReservationScheduler {

    private final SeatReservationService seatReservationService;

    /**
     * Libera automáticamente las reservas de asientos que han expirado.
     * Se ejecuta cada 30 segundos (30000 ms).
     */
    @Scheduled(fixedRate = 30000)
    public void releaseExpiredReservations() {
        try {
            log.debug("Running scheduled task: release expired seat reservations");
            seatReservationService.releaseExpiredReservations();
        } catch (Exception e) {
            log.error("Error in scheduled task for releasing expired reservations", e);
        }
    }
}
