package com.bobjool.restaurant.application.dto.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

public record RestaurantContactResDto(
    String name,
    String address,
    String phone
) {
  public static RestaurantContactResDto from(Restaurant restaurant) {
    return new RestaurantContactResDto(
        restaurant.getRestaurantName(),
        restaurant.getRestaurantAddressDetail(),
        restaurant.getRestaurantPhone()
    );
  }
}