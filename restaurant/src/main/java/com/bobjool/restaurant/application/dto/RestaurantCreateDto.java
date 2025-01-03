package com.bobjool.restaurant.application.dto;

import com.bobjool.restaurant.domain.entity.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.RestaurantRegion;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantCreateDto(
    Long userId,
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
