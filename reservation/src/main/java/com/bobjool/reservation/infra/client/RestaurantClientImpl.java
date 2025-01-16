package com.bobjool.reservation.infra.client;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.reservation.application.client.restaurant.RestaurantClient;
import com.bobjool.reservation.application.client.restaurant.RestaurantResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "restaurant-service", contextId = "restaurantClient")
public interface RestaurantClientImpl extends RestaurantClient {

    @GetMapping("/api/v1/restaurants/{restaurantId}")
    ApiResponse<RestaurantResDto> getRestaurantsForOwner(@PathVariable("restaurantId") UUID restaurantId,
                                                         @RequestHeader("X-User-Id") String userId,
                                                         @RequestHeader("X-Role") String role
    );
}
