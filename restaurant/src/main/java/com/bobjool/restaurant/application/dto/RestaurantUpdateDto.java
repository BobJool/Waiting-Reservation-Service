package com.bobjool.restaurant.application.dto;

import com.bobjool.restaurant.domain.entity.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.RestaurantRegion;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public record RestaurantUpdateDto(
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
}
