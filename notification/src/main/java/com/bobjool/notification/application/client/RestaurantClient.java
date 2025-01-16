package com.bobjool.notification.application.client;

import com.bobjool.notification.application.dto.ApiResponseDto;
import com.bobjool.notification.application.dto.RestaurantContactDto;

import java.util.UUID;

public interface RestaurantClient {
    ApiResponseDto<RestaurantContactDto> getRestaurantContact(UUID restaurantId);
}