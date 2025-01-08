package com.bobjool.payment.application.events;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationCreatedEvent(
        UUID reservationId,
        Long userId,
        UUID restaurantScheduleId,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) implements Serializable {

}
