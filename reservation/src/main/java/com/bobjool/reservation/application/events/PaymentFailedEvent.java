package com.bobjool.reservation.application.events;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID paymentId,
        UUID reservationId,
        Long userId,
        Integer amount,
        String status
){

}
