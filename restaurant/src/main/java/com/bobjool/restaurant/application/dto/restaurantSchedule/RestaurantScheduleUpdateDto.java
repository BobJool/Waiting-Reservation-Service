package com.bobjool.restaurant.application.dto.restaurantSchedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record RestaurantScheduleUpdateDto(
  LocalDate date,
  LocalTime timeSlot,
  int maxCapacity,
  int currentCapacity,
  boolean available
) {

}