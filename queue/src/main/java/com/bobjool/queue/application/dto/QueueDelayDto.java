package com.bobjool.queue.application.dto;

import java.util.UUID;

import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueType;

public record QueueDelayDto(
	UUID restaurantId,
	Long userId,
	Long targetUserId
) {
	@Override
	public String toString() {
		return "{" +
			"\"restaurantId\":\"" + restaurantId + "\"," +
			"\"userId\":\"" + userId + "\"," +
			"\"targetUserId\":\"" + targetUserId + "\"}";
	}
}
