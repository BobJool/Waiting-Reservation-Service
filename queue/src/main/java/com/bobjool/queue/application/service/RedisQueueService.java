package com.bobjool.queue.application.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.QueueDelayResDto;
import com.bobjool.queue.application.dto.QueueRegisterDto;
import com.bobjool.queue.domain.util.RedisKeyUtil;

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
		String waitingListKey  = RedisKeyUtil.getWaitingListKey(dto.restaurantId());
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(dto.restaurantId(), dto.userId());

		long position = Optional.ofNullable(redisTemplate.opsForZSet().size(waitingListKey)).orElse(0L) + 1;
		double uniqueScore = (double) position;
		redisTemplate.opsForZSet().add(waitingListKey, String.valueOf(dto.userId()), uniqueScore);

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
		redisTemplate.opsForHash().putAll(userQueueDataKey, userInfo);

		log.info("Added user {} to queue {} with position {}", dto.userId(), waitingListKey, position);
		return userInfo;
	}

	public boolean isUserAlreadyWaiting(Long userId) {
		String userIsWaitingKey = RedisKeyUtil.getUserIsWaitingKey(userId);
		return Boolean.TRUE.equals(redisTemplate.hasKey(userIsWaitingKey));
	}

	public void markUserAsWaiting(Long userId, UUID restaurantId) {
		String userIsWaitingKey = RedisKeyUtil.getUserIsWaitingKey(userId);
		redisTemplate.opsForValue().set(userIsWaitingKey, String.valueOf(restaurantId));
	}

	public Long getUserIndexInQueue(UUID restaurantId, Long userId) {
		String waitingListKey  = RedisKeyUtil.getWaitingListKey(restaurantId);

		Long index = redisTemplate.opsForZSet().rank(waitingListKey, String.valueOf(userId));
		if (index == null) {
			throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
		}
		return index;
	}

	public List<String> getNextTenUsersWithOrder(UUID restaurantId, Long userId) {
		String waitingListKey  = RedisKeyUtil.getWaitingListKey(restaurantId);
		long userRank = getUserIndexInQueue(restaurantId, userId) +1;

		Set<Object> nextUsers = redisTemplate.opsForZSet().range(waitingListKey, userRank, userRank + 9);
		if (nextUsers == null) {
			return Collections.emptyList();
		}

		List<String> result = new ArrayList<>();
		int order = (int) (userRank + 1);
		for (Object userIdObj : nextUsers) {
			long nextUserId = Long.parseLong(userIdObj.toString());
			result.add(order + "번째: " + nextUserId);
			order++;
		}
		return result;
	}

	public QueueDelayResDto delayUserRank(UUID restaurantId, Long userId, Long targetUserId) {
		String waitingListKey  = RedisKeyUtil.getWaitingListKey(restaurantId);
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(restaurantId, userId);

		Double targetScore = getUserScore(waitingListKey, targetUserId);
		Double userScore = getUserScore(waitingListKey, userId);
		if (userScore > targetScore) {
			throw new BobJoolException(ErrorCode.ALREADY_BEHIND_TARGET);
		}

		Double nextScore = calculateNextScore(waitingListKey, targetScore);
		double newScore = (targetScore + nextScore) / 2.0;
		redisTemplate.opsForZSet().add(waitingListKey, String.valueOf(userId), newScore);
		updateDelayCount(userQueueDataKey);

		Long originalPosition = getHashValue(userQueueDataKey, "position", Long.class);
		Integer member = getHashValue(userQueueDataKey, "member", Integer.class);
		long newRank = getUserIndexInQueue(restaurantId, userId) +1;

		log.info("Delayed user {} to queue {} with newRank {}", userId, waitingListKey, newRank);

		return new QueueDelayResDto(newRank, originalPosition, member);
	}

	public Long getTotalUsersInQueue(UUID restaurantId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Long totalUsers = redisTemplate.opsForZSet().size(waitingListKey);

		if (totalUsers == null || totalUsers == 0) {
			throw new BobJoolException(ErrorCode.QUEUE_EMPTY);
		}
		return totalUsers;
	}

	public void validateNotLastInQueue(UUID restaurantId, Long userId) {
		Long userRank = getUserIndexInQueue(restaurantId, userId);
		Long totalUsers = getTotalUsersInQueue(restaurantId);

		if (userRank.equals(totalUsers)) {
			throw new BobJoolException(ErrorCode.CANNOT_DELAY);
		}
	}

	public void validateDelayCount(String userQueueDataKey) {
		Integer delayCount = (Integer) redisTemplate.opsForHash().get(userQueueDataKey, "delay_count");
		if (delayCount == null) delayCount = 0;

		if (delayCount >= 2) {
			throw new BobJoolException(ErrorCode.DELAY_LIMIT_REACHED);
		}
	}

	private void updateDelayCount(String userHashKey) {
		Integer delayCount = (Integer) redisTemplate.opsForHash().get(userHashKey, "delay_count");
		redisTemplate.opsForHash().put(userHashKey, "delay_count", delayCount + 1);
	}

	private Double getUserScore(String redisKey, Long userId) {
		Double score = redisTemplate.opsForZSet().score(redisKey, String.valueOf(userId));
		if (score == null) {
			throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
		}
		return score;
	}

	private <T> T getHashValue(String hashKey, String field, Class<T> type) {
		Object value = redisTemplate.opsForHash().get(hashKey, field);
		if (value == null) {
			throw new BobJoolException(ErrorCode.QUEUE_DATA_NOT_FOUND);
		}

		if (type == Long.class) {
			return type.cast(Long.parseLong(value.toString()));
		} else if (type == Integer.class) {
			return type.cast(Integer.parseInt(value.toString()));
		} else if (type == String.class) {
			return type.cast(value.toString());
		}

		throw new IllegalArgumentException("Unsupported type: " + type.getName());
	}

	private Double calculateNextScore(String waitingListKey, Double targetScore) {
		Set<Object> nextUsers = redisTemplate.opsForZSet().rangeByScore(waitingListKey, targetScore, Double.MAX_VALUE, 1, 1);

		if (nextUsers == null || nextUsers.isEmpty()) {
			return targetScore + 1; // 다음 유저가 없으면 기본값
		}

		Object nextUser = nextUsers.iterator().next();
		return getUserScore(waitingListKey, Long.parseLong(nextUser.toString()));
	}
}
