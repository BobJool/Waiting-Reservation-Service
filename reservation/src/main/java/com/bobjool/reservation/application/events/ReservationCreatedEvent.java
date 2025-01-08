package com.bobjool.reservation.application.events;

import com.bobjool.reservation.domain.entity.Reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationCreatedEvent(
        Long userId,
        UUID restaurantScheduleId,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
    public static ReservationCreatedEvent from(Reservation reservation) {
        LocalDateTime createdAt = reservation.getCreatedAt();
        LocalDateTime expiredAt = createdAt.plusMinutes(10);

        return new ReservationCreatedEvent(
                reservation.getUserId(),
                reservation.getRestaurantScheduleId(),
                createdAt,
                expiredAt
        );
    }
}
