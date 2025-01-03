package com.bobjool.queue.infrastructure.repository;

import java.util.UUID;

public interface QueueRepositoryCustom {
	Integer findMaxPositionByRestaurantIdWithLock(UUID restaurantId);
}
