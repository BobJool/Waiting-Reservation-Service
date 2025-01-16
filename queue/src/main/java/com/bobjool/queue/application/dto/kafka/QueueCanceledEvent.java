package com.bobjool.queue.application.dto.kafka;

import java.util.UUID;

import com.bobjool.queue.domain.enums.CancelType;

public record QueueCanceledEvent(
	Long userId,
	UUID restaurantId,
	String reason
) {
	public static QueueCanceledEvent from(UUID restaurantId, Long userId, CancelType cancelType) {
		return new QueueCanceledEvent(
			userId,
			restaurantId,
			cancelType.getDescription()
		);
	}
}
