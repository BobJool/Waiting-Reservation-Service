package com.bobjool.queue.application.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisQueueService {
	private final RedisTemplate<String, String> redisTemplate;

	public boolean isUserAlreadyWaiting(Long userId) {
		String userWaitingKey = "queue:user:" + userId + ":waiting";
		return Boolean.TRUE.equals(redisTemplate.hasKey(userWaitingKey));
	}

	@Transactional
	public long addUserToQueue(UUID restaurantId, String userId) {
		String redisKey = "queue:" + restaurantId + ":usersList";
		long now = System.currentTimeMillis();
		double uniqueScore = now + ThreadLocalRandom.current().nextDouble();
		redisTemplate.opsForZSet().add(redisKey, userId, uniqueScore);
		long rank = getUserPositionInQueue(restaurantId, userId);
		log.info("Added user {} to queue {} with rank {}", userId, redisKey, rank);
		return rank;
	}

	public void markUserAsWaiting(Long userId, UUID restaurantId) {
		String userWaitingKey = "queue:user:" + userId + ":waiting";
		redisTemplate.opsForValue().set(userWaitingKey, String.valueOf(restaurantId));
		log.info("Marked user {} as waiting for restaurant {}", userId, restaurantId);
	}

	public Set<String> getAllUsersInQueue(UUID restaurantId) {
		String redisKey = "queue:" + restaurantId + ":usersList";
		return redisTemplate.opsForZSet().range(redisKey, 0, -1);
	}

	public long getUserPositionInQueue(UUID restaurantId, String userId) {
		String redisKey = "queue:" + restaurantId + ":usersList";

		Long rank = redisTemplate.opsForZSet().rank(redisKey, userId);
		if (rank == null) {
			throw new IllegalStateException("User not found in queue.");
		}
		return rank + 1;
	}

}
