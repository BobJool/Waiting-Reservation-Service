package com.bobjool.notification.infrastructure.client;

import com.bobjool.notification.application.client.RestaurantClient;
import com.bobjool.notification.application.dto.ApiResponseDto;
import com.bobjool.notification.application.dto.RestaurantContactDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "restaurant-service")
public interface RestaurantClientImpl extends RestaurantClient {
    @GetMapping("/api/v1/restaurants/{restaurantId}/contact")
    ApiResponseDto<RestaurantContactDto> getRestaurantContact(@PathVariable(name = "restaurantId") UUID restaurantId);
}
