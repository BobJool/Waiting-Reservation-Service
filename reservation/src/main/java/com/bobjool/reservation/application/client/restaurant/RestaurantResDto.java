package com.bobjool.reservation.application.client.restaurant;

import java.time.LocalTime;
import java.util.UUID;

public record RestaurantResDto(
        UUID RestaurantId,
        Long userId,
        String restaurantCategory, // RestaurantCategory
        String restaurantPhone,
        String restaurantName,
        String restaurantRegion,   // RestaurantRegion
        String restaurantAddressDetail,
        String restaurantDescription,
        int restaurantVolume,
        boolean isReservation,
        boolean isQueue,
        LocalTime openTime,
        LocalTime closeTime
) {
}
