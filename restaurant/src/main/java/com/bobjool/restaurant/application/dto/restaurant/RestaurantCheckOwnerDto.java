package com.bobjool.restaurant.application.dto.restaurant;

import java.util.UUID;

public record RestaurantCheckOwnerDto(
        Long userId,
        UUID restaurantId
) {
}
