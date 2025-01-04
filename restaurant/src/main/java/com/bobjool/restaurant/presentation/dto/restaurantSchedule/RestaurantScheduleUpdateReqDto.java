package com.bobjool.restaurant.presentation.dto.restaurantSchedule;

import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleUpdateDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public record RestaurantScheduleUpdateReqDto(

  @NotNull(message = "유저 ID 는 필수 입력값입니다.")
  @Positive(message = "유저 ID 는 양수여야 합니다.")
  Long userId,

  @NotNull(message = "테이블 번호를 입력하여야 합니다.")
  @Positive(message = "테이블 번호는 양수여야 합니다.")
  int tableNumber,

  @NotNull(message = "예약 날짜는 필수값 입니다.")
  LocalDate date,

  @NotNull(message = "예약 시간대는 필수값 입니다.")
  LocalTime timeSlot,

  @NotNull(message = "테이블 최대 인원수는 필수값 입니다.")
  int maxCapacity,

  @NotNull(message = "테이블 예약 인원수는 필수값 입니다.")
  int currentCapacity,

  @NotNull(message = "예약 상태를 설정해주셔야 합니다.")
  boolean available

) {

    public RestaurantScheduleUpdateDto toServiceDto() {
      return new RestaurantScheduleUpdateDto(
          userId,
          tableNumber,
          date,
          timeSlot,
          maxCapacity,
          currentCapacity,
          available
      );
    }
}
