package com.bobjool.restaurant.application.dto.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RestaurantForMasterResDto(
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
    LocalTime closeTime,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime updatedAt,
    String updatedBy,
    LocalDateTime deletedAt,
    String deletedBy,
    boolean isDeleted
) {

  public static RestaurantForMasterResDto from(Restaurant restaurant){
    return new RestaurantForMasterResDto(
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
        restaurant.getCloseTime(),
        restaurant.getCreatedAt(),
        restaurant.getCreatedBy(),
        restaurant.getUpdatedAt(),
        restaurant.getUpdatedBy(),
        restaurant.getDeletedAt(),
        restaurant.getDeletedBy(),
        restaurant.getIsDeleted()
    );
  }



}
