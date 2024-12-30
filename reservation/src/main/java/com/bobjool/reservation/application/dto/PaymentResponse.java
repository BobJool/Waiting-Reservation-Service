package com.bobjool.reservation.application.dto;

import com.bobjool.reservation.domain.entity.Payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID PaymentId,
        UUID reservationId,
        Long userId,
        Integer amount,
        String method,
        String pgName,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getReservationId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getMethod().name(),
                payment.getPgName().name(),
                payment.getStatus().name(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

}
