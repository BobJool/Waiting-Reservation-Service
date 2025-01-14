package com.bobjool.restaurant.application.dto.restaurantSchedule;

import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantScheduleForCustomerResDto(
    UUID restaurantId,
    int tableNumber,
    LocalDate date,
    LocalTime timeSlot,
    int currentCapacity

) {

  public static RestaurantScheduleForCustomerResDto from(RestaurantSchedule restaurantSchedule){
    return new RestaurantScheduleForCustomerResDto(
        restaurantSchedule.getRestaurantId(),
        restaurantSchedule.getTableNumber(),
        restaurantSchedule.getDate(),
        restaurantSchedule.getTimeSlot(),
        restaurantSchedule.getCurrentCapacity()
    );
  }

}
