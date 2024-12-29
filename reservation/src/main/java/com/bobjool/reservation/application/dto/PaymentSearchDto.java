package com.bobjool.reservation.application.dto;

import java.time.LocalDate;

public record PaymentSearchDto(
        Long userId,
        String status,
        LocalDate startDate,
        LocalDate endDate
) {
}
