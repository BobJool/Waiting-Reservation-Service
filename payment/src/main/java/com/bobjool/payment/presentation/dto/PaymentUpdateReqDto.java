package com.bobjool.payment.presentation.dto;

import com.bobjool.payment.application.dto.PaymentUpdateDto;
import jakarta.validation.constraints.NotBlank;

public record PaymentUpdateReqDto(
        @NotBlank(message = "변경할 결제 상태는 필수 입력값입니다.") String status
) {
    public PaymentUpdateDto toServiceDto() {
        return new PaymentUpdateDto(status);
    }
}
