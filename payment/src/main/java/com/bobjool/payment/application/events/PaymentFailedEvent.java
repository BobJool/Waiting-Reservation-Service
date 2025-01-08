package com.bobjool.payment.application.events;

import com.bobjool.payment.domain.entity.Payment;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID paymentId,
        UUID reservationId,
        Long userId,
        Integer amount,
        String status
) {
    public static PaymentFailedEvent from(Payment payment) {
        return new PaymentFailedEvent(
                payment.getId(),
                payment.getReservationId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus().name()
        );
    }
}
