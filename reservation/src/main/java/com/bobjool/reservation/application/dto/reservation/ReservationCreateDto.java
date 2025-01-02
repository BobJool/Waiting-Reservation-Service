package com.bobjool.reservation.application.dto.reservation;

import java.util.UUID;

public record ReservationCreateDto(
        Long userId,
        UUID restaurantId,
        UUID restaurantScheduleId,
        Integer guestCount
) {
}
