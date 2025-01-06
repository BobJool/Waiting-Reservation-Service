package com.bobjool.queue.application.dto;

import java.util.UUID;

public record QueueCancelDto(
	UUID restaurantId,
	Long userId,
	Long targetUserId
) { }
