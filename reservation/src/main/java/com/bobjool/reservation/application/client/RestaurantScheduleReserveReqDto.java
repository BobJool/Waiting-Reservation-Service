package com.bobjool.reservation.application.client;


public record RestaurantScheduleReserveReqDto(
        Long userId,
        int currentCapacity
) {

}
