package com.bobjool.restaurant.application.dto.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;

public record RestaurantContactResDto(
    String name,
    String address,
    String number // Feign -> Noti Service
) {
  public static RestaurantContactResDto from(Restaurant restaurant) {
    return new RestaurantContactResDto(
        restaurant.getRestaurantName(),
        restaurant.getRestaurantAddressDetail(),
        restaurant.getRestaurantPhone()
    );
  }
}