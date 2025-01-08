package com.bobjool.reservation.application.events;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID paymentId,
        UUID reservationId,
        Long userId,
        Integer amount,
        String status
) {

}
