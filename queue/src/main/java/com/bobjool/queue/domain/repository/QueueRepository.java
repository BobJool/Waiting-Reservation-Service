package com.bobjool.queue.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.bobjool.queue.domain.entity.Queue;

import jakarta.persistence.LockModeType;

@Repository
public interface QueueRepository {
	Queue save(Queue queue);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Integer findMaxPositionByRestaurantIdWithLock(UUID restaurantId);

	List<Queue> findByRestaurantId(UUID restaurantId);

	void deleteAll();
}
