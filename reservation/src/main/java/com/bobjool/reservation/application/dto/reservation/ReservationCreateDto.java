package com.bobjool.reservation.application.dto.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationCreateDto(
        Long userId,
        UUID restaurantId,
        UUID restaurantScheduleId,
        Integer guestCount,
        LocalDate reservationDate,
        LocalTime reservationTime
) {
}
