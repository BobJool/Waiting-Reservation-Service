package com.bobjool.reservation.application.client.restaurant;

import com.bobjool.common.presentation.ApiResponse;

import java.util.UUID;

public interface RestaurantClient {
    ApiResponse<RestaurantResDto> getRestaurantsForOwner(UUID restaurantId, String userId, String role);
}
