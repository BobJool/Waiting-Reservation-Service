package com.bobjool.restaurant.presentation.dto.restaurantSchedule;

import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleReserveDto;
import jakarta.validation.constraints.NotNull;

public record RestaurantScheduleReserveReqDto(

    @NotNull(message = "유저 ID 는 필수 입력값입니다.")
    Long userId,

    @NotNull(message = "테이블 예약 인원수는 필수값 입니다.")
    int currentCapacity
) {

  public RestaurantScheduleReserveDto toServiceDto() {
    return new RestaurantScheduleReserveDto(
        userId,
        currentCapacity
    );
  }
}
