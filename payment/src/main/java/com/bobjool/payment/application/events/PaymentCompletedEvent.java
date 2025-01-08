package com.bobjool.payment.application.events;

import com.bobjool.payment.domain.entity.Payment;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID paymentId,
        UUID reservationId,
        Long userId,
        Integer amount,
        String status
) {
    public static PaymentCompletedEvent from(Payment payment) {
        return new PaymentCompletedEvent(
                payment.getId(),
                payment.getReservationId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getStatus().name()
        );
    }
}
