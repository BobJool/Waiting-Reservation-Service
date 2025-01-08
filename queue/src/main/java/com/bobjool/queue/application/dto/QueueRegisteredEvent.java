package com.bobjool.queue.application.dto;

import java.util.Map;
import java.util.UUID;

public record QueueRegisteredEvent(
	Long userId,
	UUID restaurantId,
	Long rank,
	Long position,
	Integer member
) {
	public static QueueRegisteredEvent from(Long userId, UUID restaurantId, Map<String, Object> userinfo, Long rank) {
		return new QueueRegisteredEvent(
			userId,
			restaurantId,
			rank,
			(Long)userinfo.get("position"),
			(Integer)userinfo.get("member")
		);
	}
}
