package com.bobjool.reservation.application.events;

import com.bobjool.reservation.domain.entity.Reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationCreatedEvent(
        Long userId,
        UUID restaurantScheduleId,
        LocalDateTime createdAt
) {
    public static ReservationCreatedEvent from(Reservation reservation) {
        return new ReservationCreatedEvent(
                reservation.getUserId(),
                reservation.getRestaurantScheduleId(),
                reservation.getCreatedAt()
        );
    }
}
