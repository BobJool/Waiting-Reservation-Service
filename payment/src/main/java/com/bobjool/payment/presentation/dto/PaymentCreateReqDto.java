package com.bobjool.payment.presentation.dto;

import com.bobjool.payment.application.dto.PaymentCreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record PaymentCreateReqDto(
        @NotNull(message = "예약 ID 는 필수 입력값입니다.")
        UUID reservationId,
        @NotNull(message = "유저 ID 는 필수 입력값입니다.")
        @Positive(message = "유저 ID 는 양수여야 합니다.")
        Long userId,
        @NotNull(message = "결제 금액은 필수 입력값입니다.")
        @Positive(message = "결제 금액은 양수여야 합니다.")
        Integer amount,
        @NotBlank(message = "결제 방식은 필수 입력값입니다.")
        String method,
        @NotBlank(message = "PG사는 필수 입력값입니다.")
        String pgName
) {

    public PaymentCreateDto toServiceDto() {
        return new PaymentCreateDto(reservationId, userId, amount, method, pgName);
    }
}
