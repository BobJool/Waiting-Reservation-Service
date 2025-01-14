package com.bobjool.restaurant.presentation.dto.restaurantSchedule;

import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleCreateDto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantScheduleCreateReqDto(
    @NotNull(message = "레스트랑 ID는 필수값 입니다.")
    UUID restaurantId,

    @NotNull(message = "테이블 번호를 입력하여야 합니다.")
    int tableNumber,

    @NotNull(message = "예약 날짜는 필수값 입니다.")
    LocalDate date,

    @NotNull(message = "예약 시간대는 필수값 입니다.")
    LocalTime timeSlot,

    @NotNull(message = "테이블 최대 인원수는 필수값 입니다.")
    int maxCapacity
) {

  public RestaurantScheduleCreateDto toServiceDto() {
    return new RestaurantScheduleCreateDto(
        restaurantId,
        tableNumber,
        date,
        timeSlot,
        maxCapacity
    );
  }
}