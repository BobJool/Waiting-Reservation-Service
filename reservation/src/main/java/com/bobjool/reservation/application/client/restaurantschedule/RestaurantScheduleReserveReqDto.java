package com.bobjool.reservation.application.client.restaurantschedule;


public record RestaurantScheduleReserveReqDto(
        Long userId,
        int currentCapacity
) {

}
