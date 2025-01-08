package com.bobjool.reservation.application.events;

import com.bobjool.reservation.domain.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationCompletedEvent(
        Long userId,
        UUID restaurantId,
        LocalDate date,
        LocalTime time,
        Integer count,
        String status // 예약 상태: "COMPLETE"
) {
    public static ReservationCompletedEvent from(Reservation reservation) {
        return new ReservationCompletedEvent(
            reservation.getUserId(),
            reservation.getRestaurantId(),
            reservation.getReservationDate(),
            reservation.getReservationTime(),
                reservation.getGuestCount(),
                reservation.getStatus().name()
        );
    }
}
