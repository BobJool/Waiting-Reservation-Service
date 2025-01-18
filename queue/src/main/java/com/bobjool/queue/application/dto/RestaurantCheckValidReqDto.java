package com.bobjool.queue.application.dto;

import java.util.UUID;

public record RestaurantCheckValidReqDto(
	UUID restaurantId
) {
	public static RestaurantCheckValidReqDto from(UUID restaurantId) {
		return new RestaurantCheckValidReqDto(
			restaurantId
		);
	}
}
