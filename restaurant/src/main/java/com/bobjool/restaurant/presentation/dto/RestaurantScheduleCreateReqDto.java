package com.bobjool.restaurant.presentation.dto;

import com.bobjool.restaurant.application.dto.restaurant.RestaurantCreateDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleCreateDto;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantScheduleCreateReqDto(
    @NotNull(message = "레스트랑 ID는 필수값 입니다.")
    UUID restaurantId,

    @NotNull(message = "유저 ID 는 필수 입력값입니다.")
    @Positive(message = "유저 ID 는 양수여야 합니다.")
    Long userId,

    @NotNull(message = "테이블 번호를 입력하여야 합니다.")
    int tableNumber,

    @NotNull(message = "예약 날짜는 필수값 입니다.")
    LocalDate date,

    @NotNull(message = "예약 시간대는 필수값 입니다.")
    LocalTime timeSlot,

    @NotNull(message = "테이블 최대 인원수는 필수값 입니다.")
    int maxTableCapacity
) {

  public RestaurantScheduleCreateDto toServiceDto() {
    return new RestaurantScheduleCreateDto(
        userId,
        restaurantId,
        tableNumber,
        date,
        timeSlot,
        maxTableCapacity
    );
  }
}