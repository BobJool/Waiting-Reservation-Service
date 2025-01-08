package com.bobjool.payment.application.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationCreatedEvent(
        Long userId,
        UUID restaurantScheduleId,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {

}
