package com.bobjool.reservation.presentation.dto.reservation;

import com.bobjool.reservation.application.dto.reservation.ReservationCreateDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ReservationCreateReqDto(
        @NotNull(message = "유저 ID 는 필수 입력값입니다.")
        @Positive(message = "유저 ID 는 양수여야 합니다.")
        Long userId,
        @NotNull(message = "레스토랑 ID 는 필수 입력값입니다.")
        UUID restaurantId,
        @NotNull(message = "레스토랑 스케줄 ID 는 필수 입력값입니다.")
        UUID restaurantScheduleId,
        @NotNull(message = "예약 인원수는 필수 입력값입니다.")
        @Positive(message = "예약 인원수는 양수여야 합니다.")
        Integer guestCount
) {
    public ReservationCreateDto toServiceDto() {
        return new ReservationCreateDto(
                userId,
                restaurantId,
                restaurantScheduleId,
                guestCount
        );
    }
}
