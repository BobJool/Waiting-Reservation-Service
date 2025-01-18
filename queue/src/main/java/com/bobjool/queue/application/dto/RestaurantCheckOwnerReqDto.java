package com.bobjool.queue.application.dto;

import java.util.UUID;

public record RestaurantCheckOwnerReqDto(
	Long userId,
	UUID restaurantId
) {
	public static RestaurantCheckOwnerReqDto from(Long userId, UUID restaurantId) {
		return new RestaurantCheckOwnerReqDto(
			userId,
			restaurantId
		);
	}
}
