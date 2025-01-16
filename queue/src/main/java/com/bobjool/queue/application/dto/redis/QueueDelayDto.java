package com.bobjool.queue.application.dto.redis;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QueueDelayDto(

	@JsonProperty("restaurantId")
	UUID restaurantId,
	@JsonProperty("userId")
	Long userId,
	@JsonProperty("targetUserId")
	Long targetUserId
) {
}
