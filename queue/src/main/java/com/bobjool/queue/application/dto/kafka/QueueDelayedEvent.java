package com.bobjool.queue.application.dto.kafka;

import java.util.UUID;

public record QueueDelayedEvent(
	Long userId,
	UUID restaurantId,
	Long rank,
	Long position,
	Integer member
) {
	public static QueueDelayedEvent from(Long userId, UUID restaurantId, Long rank, Long position, Integer member) {
		return new QueueDelayedEvent(
			userId,
			restaurantId,
			rank,
			position,
			member
		);
	}
}
