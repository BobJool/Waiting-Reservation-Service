package com.bobjool.restaurant.application.dto.restaurantSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantScheduleReserveDto(
    UUID RestaurantScheduleId,
    Long userId,
    int tableNumber,
    LocalDate date,
    LocalTime timeSlot,
    int currentCapacity
) {

}
