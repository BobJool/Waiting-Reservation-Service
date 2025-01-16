package com.bobjool.restaurant.application.dto.restaurantSchedule;

import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantScheduleResDto(
    UUID restaurantScheduleId,
    Long userId,
    UUID restaurantId,
    int tableNumber,
    LocalDate date,
    LocalTime timeSlot,
    int maxCapacity,
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
        restaurantSchedule.getMaxCapacity(),
        restaurantSchedule.getCurrentCapacity(),
        restaurantSchedule.isAvailable()
    );
  }

}
