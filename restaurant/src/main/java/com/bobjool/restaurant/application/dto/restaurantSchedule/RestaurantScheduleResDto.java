package com.bobjool.restaurant.application.dto.restaurantSchedule;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RestaurantScheduleResDto(
    UUID RestaurantScheduleId,
    Long userId,
    UUID restaurantId,
    int tableNumber,
    LocalDate date,
    LocalTime timeSlot,
    int maxTableCapacity,
    int currentCapacity,
    boolean available

) {

  public static RestaurantScheduleResDto from(RestaurantSchedule restaurantSchedule){
    return new RestaurantScheduleResDto(
        restaurantSchedule.getId(),
        restaurantSchedule.getUserId(),
        restaurantSchedule.getRestaurantId(),
        restaurantSchedule.getTableNumber(),
        restaurantSchedule.getDate(),
        restaurantSchedule.getTimeSlot(),
        restaurantSchedule.getMaxTableCapacity(),
        restaurantSchedule.getCurrentCapacity(),
        restaurantSchedule.isAvailable()
    );
  }

}
