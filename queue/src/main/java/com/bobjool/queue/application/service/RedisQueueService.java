package com.bobjool.queue.application.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.redis.QueueRegisterDto;
import com.bobjool.queue.application.dto.kafka.QueueAlertedEvent;
import com.bobjool.queue.application.dto.kafka.QueueCanceledEvent;
import com.bobjool.queue.application.dto.kafka.QueueDelayedEvent;
import com.bobjool.queue.application.dto.kafka.QueueRegisteredEvent;
import com.bobjool.queue.application.dto.kafka.QueueRemindEvent;
import com.bobjool.queue.application.dto.redis.QueueAlertDto;
import com.bobjool.queue.application.dto.redis.QueueCancelDto;
import com.bobjool.queue.application.dto.redis.QueueCheckInDto;
import com.bobjool.queue.domain.enums.QueueStatus;
import com.bobjool.queue.domain.util.RedisKeyUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisQueueService {
	private final RedisTemplate<String, Object> redisTemplate;

	// 대기 큐에 사용자 추가
	public QueueRegisteredEvent addUserToQueue(QueueRegisterDto dto) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(dto.restaurantId());
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(dto.restaurantId(), dto.userId());

		long position = Optional.ofNullable(redisTemplate.opsForZSet().size(waitingListKey)).orElse(0L) + 1;
		double uniqueScore = (double)position;
		redisTemplate.opsForZSet().add(waitingListKey, String.valueOf(dto.userId()), uniqueScore);

		Map<String, Object> userInfo = Map.of(
			"restaurant_id", dto.restaurantId(),
			"user_id", dto.userId(),
			"member", dto.member(),
			"type", dto.type(),
			"dining_option", dto.diningOption(),
			"position", position,
			"status", QueueStatus.WAITING,
			"delay_count", 0,
			"created_at", System.currentTimeMillis()
		);
		redisTemplate.opsForHash().putAll(userQueueDataKey, userInfo);
		log.info("Added user {} to queue {} with position {}", dto.userId(), waitingListKey, position);
		markUserAsWaiting(dto.userId(), dto.restaurantId());
		long rank = getUserIndexInQueue(dto.restaurantId(), dto.userId()) + 1;
		return QueueRegisteredEvent.from(dto.userId(), dto.restaurantId(), position, rank, dto.member());
	}

	public boolean isUserWaiting(Long userId) {
		String userIsWaitingKey = RedisKeyUtil.getUserIsWaitingKey(userId);
		return Boolean.TRUE.equals(redisTemplate.hasKey(userIsWaitingKey));
	}

	public boolean isUserInQueue(UUID restaurantId, Long userId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		return Boolean.TRUE.equals(redisTemplate.opsForZSet().score(waitingListKey, String.valueOf(userId)) != null);
	}

	public void markUserAsWaiting(Long userId, UUID restaurantId) {
		String userIsWaitingKey = RedisKeyUtil.getUserIsWaitingKey(userId);
		redisTemplate.opsForValue().set(userIsWaitingKey, String.valueOf(restaurantId));
	}

	public Long getUserIndexInQueue(UUID restaurantId, Long userId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);

		Long index = redisTemplate.opsForZSet().rank(waitingListKey, String.valueOf(userId));
		if (index == null) {
			throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
		}
		return index;
	}

	public List<String> getNextTenUsersWithOrder(UUID restaurantId, Long userId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		long userRank = getUserIndexInQueue(restaurantId, userId) + 1;

		Set<Object> nextUsers = redisTemplate.opsForZSet().range(waitingListKey, userRank, userRank + 9);
		if (nextUsers == null) {
			return Collections.emptyList();
		}

		List<String> result = new ArrayList<>();
		int order = (int)(userRank + 1);
		for (Object userIdObj : nextUsers) {
			long nextUserId = Long.parseLong(userIdObj.toString());
			result.add(order + "번째: " + nextUserId);
			order++;
		}
		return result;
	}

	public QueueDelayedEvent delayUserRank(UUID restaurantId, Long userId, Long targetUserId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
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
		updateQueueStatus(restaurantId, userId, QueueStatus.DELAYED);

		Long originalPosition = getHashValue(restaurantId, userId, "position", Long.class);
		Integer member = getHashValue(restaurantId, userId, "member", Integer.class);
		Long newRank = getUserIndexInQueue(restaurantId, userId) + 1;

		log.info("Delayed user {} to queue {} with newRank {}", userId, waitingListKey, newRank);

		return QueueDelayedEvent.from(userId, restaurantId, newRank, originalPosition, member);
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
		Long userRank = getUserIndexInQueue(restaurantId, userId) + 1;
		Long totalUsers = getTotalUsersInQueue(restaurantId);

		if (userRank.equals(totalUsers)) {
			throw new BobJoolException(ErrorCode.CANNOT_DELAY);
		}
	}

	public void validateDelayCount(UUID restaurantId, Long userId) {
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(restaurantId, userId);
		Integer delayCount = (Integer)redisTemplate.opsForHash().get(userQueueDataKey, "delay_count");
		if (delayCount == null)
			delayCount = 0;

		if (delayCount >= 2) {
			throw new BobJoolException(ErrorCode.DELAY_LIMIT_REACHED);
		}
	}

	private void updateDelayCount(String userHashKey) {
		Integer delayCount = (Integer)redisTemplate.opsForHash().get(userHashKey, "delay_count");
		if (delayCount == null) {
			delayCount = 0;
		}
		redisTemplate.opsForHash().put(userHashKey, "delay_count", delayCount + 1);
	}

	private Double getUserScore(String redisKey, Long userId) {
		Double score = redisTemplate.opsForZSet().score(redisKey, String.valueOf(userId));
		if (score == null) {
			throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
		}
		return score;
	}

	private <T> T getHashValue(UUID restaurantId, Long userId, String field, Class<T> type) {
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(restaurantId, userId);
		Object value = redisTemplate.opsForHash().get(userQueueDataKey, field);
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
		Set<Object> nextUsers = redisTemplate.opsForZSet()
			.rangeByScore(waitingListKey, targetScore, Double.MAX_VALUE, 1, 1);

		if (nextUsers == null || nextUsers.isEmpty()) {
			return targetScore + 1; // 다음 유저가 없으면 기본값
		}

		Object nextUser = nextUsers.iterator().next();
		return getUserScore(waitingListKey, Long.parseLong(nextUser.toString()));
	}

	public QueueCanceledEvent cancelWaiting(QueueCancelDto dto) {
		removeUserIsWaitingKey(dto.userId());
		removeUserFromQueue(dto.restaurantId(), dto.userId());
		updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.CANCELED);
		return QueueCanceledEvent.from(dto.restaurantId(), dto.userId(), dto.reason());
	}

	private void removeUserFromQueue(UUID restaurantId, Long userId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Double score = redisTemplate.opsForZSet().score(waitingListKey, String.valueOf(userId));
		if (score != null) {
			redisTemplate.opsForZSet().remove(waitingListKey, String.valueOf(userId));
		} else {
			throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
		}
	}

	public void removeUserIsWaitingKey(Long userId) {
		String isWaitingKey = RedisKeyUtil.getUserIsWaitingKey(userId);
		if (isUserWaiting(userId)) {
			redisTemplate.delete(isWaitingKey);
		} else {
			throw new BobJoolException(ErrorCode.USER_IS_NOT_WAITING);
		}
	}

	private void updateQueueStatus(UUID restaurantId, Long userId, QueueStatus newStatus) {
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(restaurantId, userId);
		if (Boolean.TRUE.equals(redisTemplate.hasKey(userQueueDataKey))) {
			redisTemplate.opsForHash().put(userQueueDataKey, "status", newStatus);
		} else {
			throw new BobJoolException(ErrorCode.QUEUE_DATA_NOT_FOUND);
		}
	}

	public QueueRemindEvent checkInRestaurant(QueueCheckInDto dto) {
		removeUserIsWaitingKey(dto.userId());
		removeUserFromQueue(dto.restaurantId(), dto.userId());
		updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.CHECK_IN);
		return getThirdUserInfo(dto.restaurantId());
	}

	public QueueAlertedEvent sendAlertNotification(QueueAlertDto dto) {
		updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.ALERTED);
		Long originalPosition = getHashValue(dto.restaurantId(), dto.userId(), "position", Long.class);
		return QueueAlertedEvent.from(dto.userId(), dto.restaurantId(), originalPosition);
	}

	public QueueAlertedEvent sendRushAlertNotification(QueueAlertDto dto) {
		updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.RUSH_SENT);
		addUserToAutoCancelQueue(dto.restaurantId(), dto.userId());
		Long originalPosition = getHashValue(dto.restaurantId(), dto.userId(), "position", Long.class);
		return QueueAlertedEvent.from(dto.userId(), dto.restaurantId(), originalPosition);
	}

	public void checkUserStatus(UUID restaurantId, Long userId, String process) {
		String status = getHashValue(restaurantId, userId, "status", String.class);
		if (process.equals("alert")) {
			if (status == null || List.of("ALERTED", "RUSH_SENT", "CHECK_IN", "CANCELED").contains(status)) {
				throw new BobJoolException(ErrorCode.ALREADY_SENT_ALERT);
			}
		} else if (process.equals("rush")) {
			if (status == null || List.of("RUSH_SENT", "CHECK_IN", "CANCELED").contains(status)) {
				throw new BobJoolException(ErrorCode.ALREADY_SENT_RUSH_ALERT);
			}
		}
	}

	public void addUserToAutoCancelQueue(UUID restaurantId, Long userId) {
		String key = RedisKeyUtil.getAutoCancelKey(restaurantId, userId);
		redisTemplate.opsForValue().set(key, "", Duration.ofMinutes(10)); // TTL 10분 설정
		log.info("재촉 알람 발행 시, 10분 자동취소 되도록 rediskey 적재");
	}

	public QueueRemindEvent getThirdUserInfo(UUID restaurantId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Set<Object> thirdUserSet = redisTemplate.opsForZSet().range(waitingListKey, 2, 2);
		if (thirdUserSet != null && !thirdUserSet.isEmpty()) {
			Object thirdUserInfo = thirdUserSet.iterator().next();
			try {
				long userId = Long.parseLong(thirdUserInfo.toString());
				Long position = getHashValue(restaurantId, userId, "position", Long.class);
				return QueueRemindEvent.from(userId, restaurantId, 3L, position);
			} catch (NumberFormatException e) {
				throw new BobJoolException(ErrorCode.INVALID_USER_ID_FORMAT);
			}
		} else {
			throw new BobJoolException(ErrorCode.QUEUE_DATA_NOT_FOUND);
		}
	}

}
