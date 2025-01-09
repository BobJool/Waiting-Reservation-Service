package com.bobjool.payment.application.events;

import java.util.UUID;

public record PaymentTimeoutEvent(
        UUID reservationId
) {
}
