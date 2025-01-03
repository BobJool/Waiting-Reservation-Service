package com.bobjool.reservation.application.dto.payment;

import java.util.UUID;

public record PaymentCreateDto(
        UUID reservationId,
        Long userId,
        Integer amount,
        String method,
        String PgName
) {
}
