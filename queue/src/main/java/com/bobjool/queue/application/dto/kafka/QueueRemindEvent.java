package com.bobjool.queue.application.dto.kafka;

import java.util.UUID;

public record QueueRemindEvent(
	Long userId,
	UUID restaurantId,
	Long rank,
	Long position
) {
	public static QueueRemindEvent from(Long userId, UUID restaurantId, Long rank, Long position) {
		return new QueueRemindEvent(
			userId,
			restaurantId,
			rank,
			position
		);
	}
}
