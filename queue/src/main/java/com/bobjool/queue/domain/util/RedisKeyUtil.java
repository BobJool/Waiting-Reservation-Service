package com.bobjool.queue.domain.util;

import java.util.UUID;

public class RedisKeyUtil {

	public static String getWaitingListKey(UUID restaurantId) {
		return String.format("queue:restaurant:%s:usersList", restaurantId);
	}

	public static String getUserIsWaitingKey(Long userId) {
		return String.format("queue:user:%d:waiting", userId);
	}

	public static String getUserQueueDataKey(UUID restaurantId, Long userId) {
		return String.format("queue:restaurant:%s:user:%d", restaurantId, userId);
	}

	public static String getFailuresKey(UUID restaurantId) {
		return String.format("queue:restaurant:%s:failures", restaurantId);
	}

	public static String getMetadataKey(UUID restaurantId) {
		return String.format("queue:metadata:%s", restaurantId);
	}

	public static String getAutoCancelKey(UUID restaurantId, Long userId) {
		return String.format("queue:autoCancel:%s:%s", restaurantId, userId);
	}
}
