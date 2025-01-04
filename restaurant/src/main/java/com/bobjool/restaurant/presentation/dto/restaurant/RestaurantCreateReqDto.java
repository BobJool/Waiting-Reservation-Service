package com.bobjool.restaurant.presentation.dto.restaurant;

import com.bobjool.restaurant.application.dto.restaurant.RestaurantCreateDto;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public record RestaurantCreateReqDto (
//      @NotNull(message = "레스토랑 ID 는 필수 입력값입니다.")
//      UUID restaurantId,

      @NotNull(message = "유저 ID 는 필수 입력값입니다.")
      @Positive(message = "유저 ID 는 양수여야 합니다.")
      Long userId,

      @NotNull(message = "레스토랑 카테고리를 입력하여야 합니다.")
      RestaurantCategory restaurantCategory,

      @NotNull(message = "레스토랑 연락 번호는 필수값 입니다.")
      String restaurantPhone,

      @NotNull(message = "레스토랑 이름은 필수 입력값입니다.")
      String restaurantName,

      @NotNull(message = "레스토랑의 지역은 필수 입력값입니다.")
      RestaurantRegion restaurantRegion,

      @NotNull(message = "레스토랑 상세주소는 필수 입력값입니다.")
      String restaurantAddressDetail,

      @NotNull(message = "레스토랑 설명은 필수 입력 값입니다.")
      String restaurantDescription,

      @NotNull(message = "식당 수용 가능 인원은 필수 입력값입니다.")
      @Positive(message = "수용 가능 인원은 양수여야 합니다.")
      int restaurantVolume,

      @NotNull(message = "식당 예약 가능 여부는 필수 설정입니다.")
      boolean isReservation,

      @NotNull(message = "식당 대기줄 가능 여부는 필수 설정입니다.")
      boolean isQueue,

      @NotNull(message = "식당 오픈 시간은 필수 설정입니다.")
      LocalTime openTime,

      @NotNull(message = "식당 마감 시간은 필수 설정입니다.")
      LocalTime closeTime
  ) {

    public RestaurantCreateDto toServiceDto() {
      return new RestaurantCreateDto(
          userId,
          restaurantCategory,
          restaurantPhone,
          restaurantName,
          restaurantRegion,
          restaurantAddressDetail,
          restaurantDescription,
          restaurantVolume,
          isReservation,
          isQueue,
          openTime,
          closeTime
          );
    }
  }
