package com.bobjool.restaurant.application.dto.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;

public record RestaurantValidResDto(
        boolean isQueue,
        boolean isDeleted
) {
    public static RestaurantValidResDto from(Restaurant restaurant) {
        return new RestaurantValidResDto(
                restaurant.isQueue(),
                restaurant.isDeleted()
        );
    }
}
