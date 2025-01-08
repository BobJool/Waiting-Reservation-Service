package com.bobjool.reservation.application.events;

import java.util.UUID;

public record PaymentTimeoutEvent(
        UUID reservationId
) {
}
