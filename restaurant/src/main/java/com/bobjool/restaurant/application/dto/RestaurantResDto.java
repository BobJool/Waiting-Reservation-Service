package com.bobjool.restaurant.application.dto;

import com.bobjool.restaurant.domain.entity.Restaurant;
import com.bobjool.restaurant.domain.entity.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.RestaurantRegion;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RestaurantResDto(
    UUID RestaurantId,
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
//    LocalDateTime createdAt,
//    LocalDateTime updatedAt

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
//        restaurant.getCreatedAt(),
//        restaurant.getUpdatedAt()
    );
  }

}
