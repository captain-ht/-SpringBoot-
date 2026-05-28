package com.library.modules.reservation.service.impl;

import com.library.modules.reservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReservationExpireScheduler {

    private final ReservationService reservationService;

    public ReservationExpireScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(cron = "0 30 0 * * ?")
    public void expireReservationsDaily() {
        reservationService.expireReservations();
        log.info("Reservation expiration scan finished");
    }
}
