package com.bobjool.restaurant.application.dto.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantForCustomerResDto(
    UUID restaurantId,
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
        restaurant.getId(),
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
