package com.bobjool.queue.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.queue.application.client.RestaurantClient;
import com.bobjool.queue.application.dto.RestaurantCheckOwnerReqDto;
import com.bobjool.queue.application.dto.RestaurantCheckValidReqDto;
import com.bobjool.queue.application.dto.RestaurantValidResDto;

@FeignClient(name = "restaurant-service")
public interface RestaurantClientImpl extends RestaurantClient {
	@PostMapping("/api/v1/restaurants/queue/valid")
	ApiResponse<RestaurantValidResDto> restaurantValidCheck(
		@RequestBody RestaurantCheckValidReqDto reqDto
	);

	@PostMapping("/api/v1/restaurants/queue/owner")
	boolean restaurantOwnerCheck(
		@RequestBody RestaurantCheckOwnerReqDto reqDto
	);

}
