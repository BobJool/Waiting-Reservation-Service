package com.bobjool.reservation.application.events;

import com.bobjool.reservation.domain.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationFailedEvent(
        Long userId,
        UUID restaurantId,
        LocalDate date,
        LocalTime time,
        Integer count,
        String status // 예약 상태: "FAIL"
) {
    public static ReservationFailedEvent from(Reservation reservation) {
        return new ReservationFailedEvent(
                reservation.getUserId(),
                reservation.getRestaurantId(),
                reservation.getReservationDate(),
                reservation.getReservationTime(),
                reservation.getGuestCount(),
                reservation.getStatus().name()
        );
    }
}
