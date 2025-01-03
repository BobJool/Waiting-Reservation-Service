package com.bobjool.restaurant.application.dto.restaurantSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantScheduleUpdateDto(
  Long userId,
  int tableNumber,
  LocalDate date,
  LocalTime timeSlot,
  int maxCapacity,
  int currentCapacity,
  boolean available
) {

}