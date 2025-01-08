package com.bobjool.queue.application.dto.kafka;

import java.util.UUID;

public record QueueRegisteredEvent(
	Long userId,
	UUID restaurantId,
	Long rank,
	Long position,
	Integer member
) {
	public static QueueRegisteredEvent from(Long userId, UUID restaurantId, Long position, Long rank, Integer member) {
		return new QueueRegisteredEvent(
			userId,
			restaurantId,
			rank,
			position,
			member
		);
	}
}
