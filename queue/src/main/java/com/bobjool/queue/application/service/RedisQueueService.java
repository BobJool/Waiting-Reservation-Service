package com.bobjool.queue.application.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.UserQueueData;
import com.bobjool.queue.application.dto.kafka.QueueAlertedEvent;
import com.bobjool.queue.application.dto.kafka.QueueCanceledEvent;
import com.bobjool.queue.application.dto.kafka.QueueDelayedEvent;
import com.bobjool.queue.application.dto.kafka.QueueRegisteredEvent;
import com.bobjool.queue.application.dto.kafka.QueueRemindEvent;
import com.bobjool.queue.application.dto.redis.QueueAlertDto;
import com.bobjool.queue.application.dto.redis.QueueCancelDto;
import com.bobjool.queue.application.dto.redis.QueueCheckInDto;
import com.bobjool.queue.application.dto.redis.QueueDelayDto;
import com.bobjool.queue.application.dto.redis.QueueRegisterDto;
import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueStatus;
import com.bobjool.queue.domain.enums.QueueType;
import com.bobjool.queue.domain.util.RedisKeyUtil;
import com.bobjool.queue.infrastructure.aspect.RedissonLock;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisQueueService {
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	@RedissonLock(value = "#dto.restaurantId")
	public QueueRegisteredEvent registerQueue(QueueRegisterDto dto) {
		try {
			long position = addToWaitingQueue(dto);
			saveUserInfoToRedis(dto, position);
			markUserAsWaiting(dto.userId(), dto.restaurantId());

			long rank = getUserIndexInQueue(dto.restaurantId(), dto.userId()) + 1;
			return QueueRegisteredEvent.from(dto.userId(), dto.restaurantId(), position, rank, dto.member());

		} catch (Exception e) {
			log.error("Error during processing: {}", e.getMessage(), e);
			log.info("Rolling back changes...");
			rollbackResisterOperations(dto);

			throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
		}
	}

	@RedissonLock(value = "#dto.restaurantId")
	public QueueDelayedEvent delayUserRank(QueueDelayDto dto) {
		UserQueueData userQueueInfo = getUserQueueData(dto.restaurantId(), dto.userId());
		Double originalScore = getUserScore(dto.restaurantId(), dto.userId());
		QueueStatus originalStatus = userQueueInfo.status();
		Long originalDelayCount = userQueueInfo.delayCount();
		try {
			updateUserScore(dto.restaurantId(), dto.userId(), dto.targetUserId());
			updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.DELAYED);
			updateDelayCount(dto.restaurantId(), dto.userId());
			Long originalPosition = userQueueInfo.position();
			Integer member = userQueueInfo.member();
			Long newRank = getUserIndexInQueue(dto.restaurantId(), dto.userId()) + 1;

			log.info("Delayed user {} to queue {} with newRank {}", dto.restaurantId(), dto.userId(), newRank);

			return QueueDelayedEvent.from(dto.userId(), dto.restaurantId(), newRank, originalPosition, member);

		} catch (Exception e) {
			log.error("Error during processing: {}", e.getMessage(), e);
			log.info("Rolling back changes...");
			rollBackDelayOperations(dto.restaurantId(), dto.userId(), originalScore, originalStatus, originalDelayCount);

			throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
		}
	}

	@RedissonLock(value = "#dto.restaurantId")
	public QueueCanceledEvent cancelWaiting(QueueCancelDto dto) {
		UserQueueData userQueueInfo = getUserQueueData(dto.restaurantId(), dto.userId());
		QueueStatus originStatus = userQueueInfo.status();
		Double originalScore = getUserScore(dto.restaurantId(), dto.userId());
		try {
			removeUserIsWaitingKey(dto.userId());
			removeUserFromQueue(dto.restaurantId(), dto.userId());
			updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.CANCELED);
			return QueueCanceledEvent.from(dto.restaurantId(), dto.userId(), dto.reason());
		} catch (Exception e) {
			log.error("Error during processing: {}", e.getMessage(), e);
			log.info("Rolling back changes...");
			rollBackCancelOperations(dto.restaurantId(), dto.userId(), originalScore, originStatus);

			throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
		}
	}

	@RedissonLock(value = "#dto.restaurantId")
	public QueueAlertedEvent sendAlertNotification(QueueAlertDto dto) {
		UserQueueData userQueueInfo = getUserQueueData(dto.restaurantId(), dto.userId());
		QueueStatus originalStatus = userQueueInfo.status();
		try {
			updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.ALERTED);
			Long originalPosition = getHashValue(dto.restaurantId(), dto.userId(), "position", Long.class);
			return QueueAlertedEvent.from(dto.userId(), dto.restaurantId(), originalPosition);
		} catch (Exception e) {
			log.error("Error during processing: {}", e.getMessage(), e);
			log.info("Rolling back changes...");
			updateQueueStatus(dto.restaurantId(), dto.userId(), originalStatus);

			throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
		}
	}

	@RedissonLock(value = "#dto.restaurantId")
	public QueueAlertedEvent sendRushAlertNotification(QueueAlertDto dto) {
		UserQueueData userQueueInfo = getUserQueueData(dto.restaurantId(), dto.userId());
		QueueStatus originalStatus = userQueueInfo.status();
		try {
			updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.RUSH_SENT);
			addUserToAutoCancelQueue(dto.restaurantId(), dto.userId());
			Long originalPosition = getHashValue(dto.restaurantId(), dto.userId(), "position", Long.class);
			return QueueAlertedEvent.from(dto.userId(), dto.restaurantId(), originalPosition);
		} catch (Exception e) {
			log.error("Error during processing: {}", e.getMessage(), e);
			log.info("Rolling back changes...");
			rollbackRushOperations(dto.restaurantId(), dto.userId(), originalStatus);

			throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
		}
	}

	private void rollbackResisterOperations(QueueRegisterDto dto) {
		removeUserIsWaitingKey(dto.userId());
		removeUserFromQueue(dto.restaurantId(), dto.userId());
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(dto.restaurantId(), dto.userId());
		redisTemplate.delete(userQueueDataKey);
	}

	private void rollBackDelayOperations(UUID restaurantId, Long userId, Double originalScore,
		QueueStatus originalStatus, Long originalDelayCount) {
		rollBackUserScore(restaurantId, userId, originalScore);
		updateQueueStatus(restaurantId, userId, originalStatus);
		rollBackDelayCount(restaurantId, userId, originalDelayCount);
	}

	private void rollBackCancelOperations(UUID restaurantId, Long userId, Double originalScore, QueueStatus originalStatus) {
		updateQueueStatus(restaurantId, userId, originalStatus);
		markUserAsWaiting(userId, restaurantId);
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		redisTemplate.opsForZSet().add(waitingListKey, String.valueOf(userId), originalScore);
	}

	private void rollbackRushOperations(UUID restaurantId, Long userId, QueueStatus originalStatus) {
		updateQueueStatus(restaurantId, userId, originalStatus);
		removeUserFromAutoCancelQueue(restaurantId, userId);
	}

	private Long addToWaitingQueue(QueueRegisterDto dto) {
		try {
			String waitingListKey = RedisKeyUtil.getWaitingListKey(dto.restaurantId());
			Long position = getPosition(dto.restaurantId());
			double uniqueScore = (double) position;
			boolean addedToQueue = Boolean.TRUE.equals(
				redisTemplate.opsForZSet().add(waitingListKey, String.valueOf(dto.userId()), uniqueScore)
			);

			if (addedToQueue) {
				return position;
			} else {
				log.error("Failed to add user {} to waiting queue", dto.userId());
				throw new BobJoolException(ErrorCode.FAILED_ADD_TO_QUEUE);
			}
		} catch (Exception e) {
			log.error("Exception occurred while adding user {} to waiting queue: {}", dto.userId(), e.getMessage(), e);
			throw new BobJoolException(ErrorCode.FAILED_ADD_TO_QUEUE);
		}
	}

	private void updateUserScore(UUID restaurantId, Long userId, Long targetUserId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Double targetScore = getUserScore(restaurantId, targetUserId);
		Double userScore = getUserScore(restaurantId, userId);
		if (userScore > targetScore) {
			throw new BobJoolException(ErrorCode.ALREADY_BEHIND_TARGET);
		}
		Double nextScore = calculateNextScore(restaurantId, targetScore);
		double newScore = (targetScore + nextScore) / 2.0;
		redisTemplate.opsForZSet().add(waitingListKey, String.valueOf(userId), newScore);
	}

	private void rollBackUserScore(UUID restaurantId, Long userId, Double userScore) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		redisTemplate.opsForZSet().add(waitingListKey, String.valueOf(userId), userScore);
	}

	private void saveUserInfoToRedis(QueueRegisterDto dto, long position) {
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(dto.restaurantId(), dto.userId());

		Map<String, Object> userInfo = objectMapper.convertValue(dto, Map.class);
		userInfo.put("position", position);
		userInfo.put("status", "WAITING");
		userInfo.put("delay_count", 0);
		userInfo.put("created_at", System.currentTimeMillis());
		try {
			redisTemplate.opsForHash().putAll(userQueueDataKey, userInfo);
		} catch (Exception e) {
			log.error("Failed to save user info to Redis for key: {}", userQueueDataKey, e);
			throw new BobJoolException(ErrorCode.FAILED_SAVE);
		}
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

	public Long getTotalUsersInQueue(UUID restaurantId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Long totalUsers = redisTemplate.opsForZSet().size(waitingListKey);

		if (totalUsers == null || totalUsers == 0) {
			throw new BobJoolException(ErrorCode.QUEUE_EMPTY);
		}
		return totalUsers;
	}

	public Long getPosition(UUID restaurantId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Long totalUsers = redisTemplate.opsForZSet().size(waitingListKey);
		if (totalUsers == null || totalUsers == 0) {
			return 1L;
		}
		return totalUsers + 1;
	}

	public Double getUserScore(UUID restaurantId, Long userId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Double score = redisTemplate.opsForZSet().score(waitingListKey, String.valueOf(userId));
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

	public boolean isUserWaiting(Long userId) {
		String userIsWaitingKey = RedisKeyUtil.getUserIsWaitingKey(userId);
		return Boolean.TRUE.equals(redisTemplate.hasKey(userIsWaitingKey));
	}

	public boolean isUserInQueue(UUID restaurantId, Long userId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		return Boolean.TRUE.equals(redisTemplate.opsForZSet().score(waitingListKey, String.valueOf(userId)) != null);
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

	private void updateDelayCount(UUID restaurantId, Long userId) {
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(restaurantId, userId);
		Integer delayCount = (Integer)redisTemplate.opsForHash().get(userQueueDataKey, "delay_count");
		if (delayCount == null) {
			delayCount = 0;
		}
		redisTemplate.opsForHash().put(userQueueDataKey, "delay_count", delayCount + 1);
	}

	private void rollBackDelayCount(UUID restaurantId, Long userId, Long delayCount) {
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(restaurantId, userId);
		redisTemplate.opsForHash().put(userQueueDataKey, "delay_count", delayCount);
	}

	public UserQueueData getUserQueueData(UUID restaurantId, Long userId) {
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(restaurantId, userId);
		Map<Object, Object> userQueueDataMap = redisTemplate.opsForHash().entries(userQueueDataKey);

		if (userQueueDataMap.isEmpty()) {
			throw new BobJoolException(ErrorCode.QUEUE_DATA_NOT_FOUND);
		}

		try {
			Long position = Long.valueOf(userQueueDataMap.getOrDefault("position", "0").toString());
			Integer member = Integer.valueOf(userQueueDataMap.getOrDefault("member", "0").toString());
			DiningOption diningOption = DiningOption.valueOf(
				userQueueDataMap.getOrDefault("dining_option", DiningOption.IN_STORE).toString());
			QueueType type = QueueType.valueOf(userQueueDataMap.getOrDefault("type", QueueType.ONLINE).toString());
			QueueStatus status = QueueStatus.valueOf(userQueueDataMap.getOrDefault("status", QueueStatus.WAITING).toString());
			long createdAt = Long.parseLong(userQueueDataMap.getOrDefault("created_at", "0").toString());
			LocalDateTime createdAtDateTime = null;
			if (createdAt > 0) {
				createdAtDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault());
			}
			Long delayCount = Long.valueOf(userQueueDataMap.getOrDefault("delay_count", "0").toString());

			return new UserQueueData(userId, restaurantId, position, member, diningOption, type, status, delayCount, createdAtDateTime);
		} catch (NumberFormatException e) {
			throw new BobJoolException(ErrorCode.INVALID_DATA_FORMAT);
		}
	}

	private Double calculateNextScore(UUID restaurantId, Double targetScore) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Set<Object> nextUsers = redisTemplate.opsForZSet()
			.rangeByScore(waitingListKey, targetScore, Double.MAX_VALUE, 1, 1);

		if (nextUsers == null || nextUsers.isEmpty()) {
			return targetScore + 1; // 다음 유저가 없으면 기본값
		}

		Object nextUser = nextUsers.iterator().next();
		return getUserScore(restaurantId, Long.parseLong(nextUser.toString()));
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

	public void removeUserFromAutoCancelQueue(UUID restaurantId, Long userId) {
		String key = RedisKeyUtil.getAutoCancelKey(restaurantId, userId);
		redisTemplate.delete(key);
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
