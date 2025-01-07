package com.bobjool.queue.application.dto;

import java.util.UUID;

import com.bobjool.queue.domain.enums.CancelType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record QueueCancelDto(

	@JsonProperty("restaurantId")
	UUID restaurantId,
	@JsonProperty("userId")
	Long userId,
	@JsonProperty("reason")
	String reason
) {
	public static QueueCancelDto of(UUID restaurantId, Long userId, CancelType cancelType) {
		return new QueueCancelDto(
			restaurantId,
			userId,
			cancelType.getDescription()
		);
	}
}
