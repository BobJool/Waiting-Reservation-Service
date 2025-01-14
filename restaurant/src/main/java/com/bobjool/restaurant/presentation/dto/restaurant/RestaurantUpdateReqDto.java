package com.bobjool.restaurant.presentation.dto.restaurant;

import com.bobjool.restaurant.application.dto.restaurant.RestaurantUpdateDto;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalTime;

public record RestaurantUpdateReqDto(

  RestaurantCategory restaurantCategory,

  String restaurantPhone,

  String restaurantName,

  RestaurantRegion restaurantRegion,

  String restaurantAddressDetail,

  String restaurantDescription,

  int restaurantVolume,

  boolean isReservation,

  boolean isQueue,

  LocalTime openTime,

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
