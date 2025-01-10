package com.bobjool.queue.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueStatus;
import com.bobjool.queue.domain.enums.QueueType;

public record UserQueueData(
	Long userId,
	UUID restaurantId,
	Long position,
	Integer member,
	DiningOption diningOption,
	QueueType type,
	QueueStatus status,
	Long delayCount,
	LocalDateTime createdAt
) {
	public static UserQueueData from(
		Long userId,
		UUID restaurantId,
		Long position,
		Integer member,
		DiningOption diningOption,
		QueueType type,
		QueueStatus status,
		Long delayCount,
		LocalDateTime createdAt
	) {
		return new UserQueueData(
			userId,
			restaurantId,
			position,
			member,
			diningOption,
			type,
			status,
			delayCount,
			createdAt
		);
	}
}
