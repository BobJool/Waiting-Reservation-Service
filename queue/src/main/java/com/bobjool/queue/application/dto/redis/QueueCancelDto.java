package com.bobjool.queue.application.dto.redis;

import java.util.UUID;

import com.bobjool.queue.domain.enums.CancelType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record QueueCancelDto(

	@JsonProperty("restaurantId")
	UUID restaurantId,
	@JsonProperty("userId")
	Long userId,
	@JsonProperty("reason")
	CancelType reason
) {
}
