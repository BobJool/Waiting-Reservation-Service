package com.bobjool.restaurant.application.dto.restaurantSchedule;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RestaurantScheduleCreateDto(
    Long userId,
    UUID restaurantId,
    int tableNumber,
    LocalDate date,
    LocalTime timeSlot,
    int maxCapacity
){

  public int reserveTime( ){
    return this.timeSlot().getHour();
  }

}
