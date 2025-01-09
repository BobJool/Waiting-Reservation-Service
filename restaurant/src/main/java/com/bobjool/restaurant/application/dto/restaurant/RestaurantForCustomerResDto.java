package com.bobjool.restaurant.application.dto.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantForCustomerResDto(
    RestaurantCategory restaurantCategory,
    String restaurantPhone,
    String restaurantName,
    RestaurantRegion restaurantRegion,
    String restaurantAddressDetail,
    String restaurantDescription,
    LocalTime openTime,
    LocalTime closeTime

) {

  public static RestaurantForCustomerResDto from(Restaurant restaurant){
    return new RestaurantForCustomerResDto(
        restaurant.getRestaurantCategory(),
        restaurant.getRestaurantPhone(),
        restaurant.getRestaurantName(),
        restaurant.getRestaurantRegion(),
        restaurant.getRestaurantAddressDetail(),
        restaurant.getRestaurantDescription(),
        restaurant.getOpenTime(),
        restaurant.getCloseTime()
    );
  }

}
