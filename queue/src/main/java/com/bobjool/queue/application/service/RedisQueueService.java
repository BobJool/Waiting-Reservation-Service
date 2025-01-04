package com.bobjool.queue.application.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.bobjool.queue.application.dto.QueueRegisterDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisQueueService {
	private final RedisTemplate<String, Object> redisTemplate;
	private final StringRedisTemplate stringRedisTemplate;

	// 메시지 발행
	public void publishMessage(String topic, String message) {
		stringRedisTemplate.convertAndSend(topic, message);
	}

	// 대기 큐에 사용자 추가
	public Map<String, Object> addUserToQueue(QueueRegisterDto dto) {
		String redisKey = "queue:restaurant:" + dto.restaurantId() + ":usersList";
		String userHashKey = "queue:restaurant:" + dto.restaurantId() + ":user:" + dto.userId();

		long position = Optional.ofNullable(redisTemplate.opsForZSet().size(redisKey)).orElse(0L) + 1;
		double uniqueScore = (double) position;
		redisTemplate.opsForZSet().add(redisKey, String.valueOf(dto.userId()), uniqueScore);

		Map<String, Object> userInfo = Map.of(
			"restaurant_id", dto.restaurantId(),
			"user_id", dto.userId(),
			"member", dto.member(),
			"type", dto.type(),
			"diningOption", dto.diningOption(),
			"position", position,
			"delay_count", 0,
			"created_at", System.currentTimeMillis()
		);
		redisTemplate.opsForHash().putAll(userHashKey, userInfo);

		log.info("Added user {} to queue {} with position {}", dto.userId(), redisKey, position);
		return userInfo;
	}

	public boolean isUserAlreadyWaiting(Long userId) {
		String userWaitingKey = "queue:user:" + userId + ":waiting";
		return Boolean.TRUE.equals(redisTemplate.hasKey(userWaitingKey));
	}

	public void markUserAsWaiting(Long userId, UUID restaurantId) {
		String userWaitingKey = "queue:user:" + userId + ":waiting";
		redisTemplate.opsForValue().set(userWaitingKey, String.valueOf(restaurantId));
		log.info("Marked user {} as waiting for restaurant {}", userId, restaurantId);
	}

	public long getUserPositionInQueue(UUID restaurantId, Long userId) {
		String redisKey = "queue:restaurant:" + restaurantId + ":usersList";

		Long rank = redisTemplate.opsForZSet().rank(redisKey, String.valueOf(userId));
		if (rank == null) {
			throw new IllegalStateException("User not found in queue.");
		}
		return rank + 1;
	}

}
