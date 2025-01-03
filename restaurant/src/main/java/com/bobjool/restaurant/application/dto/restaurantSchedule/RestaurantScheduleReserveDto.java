package com.bobjool.restaurant.application.dto.restaurantSchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantScheduleReserveDto(
    Long userId,
    int tableNumber,
    LocalDate date,
    LocalTime timeSlot,
    int currentCapacity
) {

}
