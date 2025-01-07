package com.bobjool.reservation.application.interfaces;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "RESTAURANT-SERVICE")
public interface RestaurantClient {

  @GetMapping("/api/v1/restaurants")
  ResponseEntity<ApiResponse<PageResponse<?>>> getAllRestaurants();
}
