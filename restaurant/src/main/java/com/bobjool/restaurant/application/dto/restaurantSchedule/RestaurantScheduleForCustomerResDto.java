package com.bobjool.restaurant.application.dto.restaurantSchedule;

import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
