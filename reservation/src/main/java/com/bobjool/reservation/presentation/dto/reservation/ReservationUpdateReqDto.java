package com.bobjool.reservation.presentation.dto.reservation;

import com.bobjool.reservation.application.dto.reservation.ReservationUpdateDto;
import jakarta.validation.constraints.NotNull;

public record ReservationUpdateReqDto(
        @NotNull(message = "예약 상태는 필수 입력값입니다.") String status
) {
    public ReservationUpdateDto toServiceDto() {
        return new ReservationUpdateDto(status);
    }
}
