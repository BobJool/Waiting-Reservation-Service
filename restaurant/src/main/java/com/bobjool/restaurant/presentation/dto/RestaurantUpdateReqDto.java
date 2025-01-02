package com.bobjool.restaurant.presentation.dto;

import com.bobjool.restaurant.application.dto.RestaurantCreateDto;
import com.bobjool.restaurant.application.dto.RestaurantUpdateDto;
import com.bobjool.restaurant.domain.entity.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.RestaurantRegion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalTime;

public record RestaurantUpdateReqDto(


  RestaurantCategory restaurantCategory,

//  @NotNull(message = "레스토랑 연락 번호는 필수값 입니다.")
  String restaurantPhone,

//  @NotNull(message = "레스토랑 이름은 필수 입력값입니다.")
  String restaurantName,

//  @NotNull(message = "레스토랑의 지역은 필수 입력값입니다.")
  RestaurantRegion restaurantRegion,

//  @NotNull(message = "레스토랑 상세주소는 필수 입력값입니다.")
  String restaurantAddressDetail,

//  @NotNull(message = "레스토랑 설명은 필수 입력 값입니다.")
  String restaurantDescription,

//  @NotNull(message = "식당 수용 가능 인원은 필수 입력값입니다.")
//  @Positive(message = "수용 가능 인원은 양수여야 합니다.")
  int restaurantVolume,

//  @NotNull(message = "식당 예약 가능 여부는 필수 설정입니다.")
  boolean isReservation,

//  @NotNull(message = "식당 대기줄 가능 여부는 필수 설정입니다.")
  boolean isQueue,

//  @NotNull(message = "식당 오픈 시간은 필수 설정입니다.")
  LocalTime openTime,

//  @NotNull(message = "식당 마감 시간은 필수 설정입니다.")
  LocalTime closeTime
  ) {

    public RestaurantUpdateDto toServiceDto() {
      return new RestaurantUpdateDto(
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
