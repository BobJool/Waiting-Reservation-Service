package com.bobjool.queue.application.dto.kafka;

import java.util.UUID;

public record QueueAlertedEvent(
	Long userId,
	UUID restaurantId,
	Long position
) {
	public static QueueAlertedEvent from(Long userId, UUID restaurantId, Long position) {
		return new QueueAlertedEvent(
			userId,
			restaurantId,
			position
		);
	}
}
