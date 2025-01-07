package com.bobjool.payment.application.dto;

import java.util.UUID;

public record PaymentCreateDto(
        UUID reservationId,
        Long userId,
        Integer amount,
        String method,
        String PgName
) {
}
