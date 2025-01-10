package com.bobjool.restaurant.application.dto.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantResDto(
    UUID restaurantId,
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

  public static RestaurantResDto from(Restaurant restaurant){
    return new RestaurantResDto(
        restaurant.getId(),
        restaurant.getUserId(),
        restaurant.getRestaurantCategory(),
        restaurant.getRestaurantPhone(),
        restaurant.getRestaurantName(),
        restaurant.getRestaurantRegion(),
        restaurant.getRestaurantAddressDetail(),
        restaurant.getRestaurantDescription(),
        restaurant.getRestaurantVolume(),
        restaurant.isReservation(),
        restaurant.isQueue(),
        restaurant.getOpenTime(),
        restaurant.getCloseTime()
    );
  }



}
