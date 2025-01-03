package com.bobjool.reservation.application.dto.reservation;

import java.util.UUID;

public record ReservationSearchDto(
        Long userId,
        UUID restaurantId,
        UUID restaurantScheduleId,
        String status
) {

}
