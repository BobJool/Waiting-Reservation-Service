package com.bobjool.restaurant.application.dto.restaurantSchedule;

public record RestaurantScheduleReserveDto(
    Long userId,
    int currentCapacity
) {

}
