package com.bobjool.payment.application.dto;

import com.bobjool.payment.domain.entity.Payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResDto(
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
    public static PaymentResDto from(Payment payment) {
        return new PaymentResDto(
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
