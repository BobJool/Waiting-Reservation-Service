package com.bobjool.queue.application.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisQueueService {
	private final RedisTemplate<String, Object> redisTemplate;
	private final RedissonClient redissonClient;

	public QueueRegisteredEvent registerQueue(QueueRegisterDto dto) {
		String lockKey = "registerLock:" + dto.restaurantId();
		RLock lock = redissonClient.getLock(lockKey);

		try {
			if (lock.tryLock(5, 7, TimeUnit.SECONDS)) {
				try {
					long position = getNextQueuePosition(dto.restaurantId(), dto.userId());
					addToWaitingQueue(dto, position);
					saveUserInfoToRedis(dto, position);
					markUserAsWaiting(dto.userId(), dto.restaurantId());

					long rank = getUserIndexInQueue(dto.restaurantId(), dto.userId()) + 1;
					return QueueRegisteredEvent.from(dto.userId(), dto.restaurantId(), position, rank, dto.member());

				} catch (Exception e) {
					log.error("Error during processing: {}", e.getMessage(), e);
					log.info("Rolling back changes...");
					rollbackResisterOperations(dto);

					throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
				} finally {
					if (lock.isHeldByCurrentThread()) {
						lock.unlock();
						log.info("Lock released for key: {}", lockKey);
					}
				}
			} else {
				log.warn("Failed to acquire lock for key: {}", lockKey);
				throw new BobJoolException(ErrorCode.FAILED_ACQUIRE_LOCK);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Thread interrupted while trying to acquire lock for key: {}", lockKey, e);
			throw new BobJoolException(ErrorCode.INTERRUPTED_WHILE_ACQUIRE_LOCK);
		}
	}

	public QueueDelayedEvent delayUserRank(QueueDelayDto dto) {
		String lockKey = "delayLock:" + dto.restaurantId();
		RLock lock = redissonClient.getLock(lockKey);
		try {
			if (lock.tryLock(5, 7, TimeUnit.SECONDS)) {
				try {
					updateUserScore(dto.restaurantId(), dto.userId(), dto.targetUserId());
					updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.DELAYED);
					updateDelayCount(dto.restaurantId(), dto.userId());
					UserQueueData userQueueInfo = getUserQueueData(dto.restaurantId(), dto.userId());
					Long originalPosition = userQueueInfo.position();
					Integer member = userQueueInfo.member();
					Long newRank = getUserIndexInQueue(dto.restaurantId(), dto.userId()) + 1;

					log.info("Delayed user {} to queue {} with newRank {}", dto.restaurantId(), dto.userId(), newRank);

					return QueueDelayedEvent.from(dto.userId(), dto.restaurantId(), newRank, originalPosition, member);

				} catch (Exception e) {
					log.error("Error during processing: {}", e.getMessage(), e);
					log.info("Rolling back changes...");
					//TODO 롤백로직 1. 스코어
					// 2. state(원래 status값 넘어오는 거 없음) : delayed는 웨이팅 시만 가능하다는 규칙적용하면, delay하기 전에 status 검증도 해야됨
					// 3. delaycount (원래 delaycount 넘어오는 거 없음) 현재값 조회해서 -1해서 넣어?
					// rollbackDelayOperations(dto);

					throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
				} finally {
					if (lock.isHeldByCurrentThread()) {
						lock.unlock();
						log.info("Lock released for key: {}", lockKey);
					}
				}
			} else {
				log.warn("Failed to acquire lock for key: {}", lockKey);
				throw new BobJoolException(ErrorCode.FAILED_ACQUIRE_LOCK);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Thread interrupted while trying to acquire lock for key: {}", lockKey, e);
			throw new BobJoolException(ErrorCode.INTERRUPTED_WHILE_ACQUIRE_LOCK);
		}
	}

	public QueueCanceledEvent cancelWaiting(QueueCancelDto dto) {
		String lockKey = "cancelLock:" + dto.restaurantId();
		RLock lock = redissonClient.getLock(lockKey);
		try {
			if (lock.tryLock(5, 7, TimeUnit.SECONDS)) {
				try {
					removeUserIsWaitingKey(dto.userId());
					removeUserFromQueue(dto.restaurantId(), dto.userId());
					updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.CANCELED);
					return QueueCanceledEvent.from(dto.restaurantId(), dto.userId(), dto.reason());
				} catch (Exception e) {
					log.error("Error during processing: {}", e.getMessage(), e);
					log.info("Rolling back changes...");
					//TODO 대기 취소 롤백로직
					// 1. QueueData를 가져와서 isWaiting 으로 다시 만들고, waitingList에도 다시추가
					// 문제는 원래 status(어떤 상태든 canceled로 만들수 있으니)와 원래 score를 알아야 한다는것
					// rollbackCancelOperations(dto);

					throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
				} finally {
					if (lock.isHeldByCurrentThread()) {
						lock.unlock();
						log.info("Lock released for key: {}", lockKey);
					}
				}
			} else {
				log.warn("Failed to acquire lock for key: {}", lockKey);
				throw new BobJoolException(ErrorCode.FAILED_ACQUIRE_LOCK);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Thread interrupted while trying to acquire lock for key: {}", lockKey, e);
			throw new BobJoolException(ErrorCode.INTERRUPTED_WHILE_ACQUIRE_LOCK);
		}
	}

	public QueueAlertedEvent sendAlertNotification(QueueAlertDto dto) {
		String lockKey = "alertLock:" + dto.restaurantId();
		RLock lock = redissonClient.getLock(lockKey);
		try {
			if (lock.tryLock(5, 7, TimeUnit.SECONDS)) {
				try {
					updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.ALERTED);
					Long originalPosition = getHashValue(dto.restaurantId(), dto.userId(), "position", Long.class);
					return QueueAlertedEvent.from(dto.userId(), dto.restaurantId(), originalPosition);
				} catch (Exception e) {
					log.error("Error during processing: {}", e.getMessage(), e);
					log.info("Rolling back changes...");
					//TODO 입장알림 취소 롤백로직
					// 1. 원래 status로 돌려주기..
					// rollbackAlertOperations(dto);

					throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
				} finally {
					if (lock.isHeldByCurrentThread()) {
						lock.unlock();
						log.info("Lock released for key: {}", lockKey);
					}
				}
			} else {
				log.warn("Failed to acquire lock for key: {}", lockKey);
				throw new BobJoolException(ErrorCode.FAILED_ACQUIRE_LOCK);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Thread interrupted while trying to acquire lock for key: {}", lockKey, e);
			throw new BobJoolException(ErrorCode.INTERRUPTED_WHILE_ACQUIRE_LOCK);
		}
	}

	public QueueAlertedEvent sendRushAlertNotification(QueueAlertDto dto) {
		String lockKey = "rushAlertLock:" + dto.restaurantId();
		RLock lock = redissonClient.getLock(lockKey);
		try {
			if (lock.tryLock(5, 7, TimeUnit.SECONDS)) {
				try {
					updateQueueStatus(dto.restaurantId(), dto.userId(), QueueStatus.RUSH_SENT);
					addUserToAutoCancelQueue(dto.restaurantId(), dto.userId());
					Long originalPosition = getHashValue(dto.restaurantId(), dto.userId(), "position", Long.class);
					return QueueAlertedEvent.from(dto.userId(), dto.restaurantId(), originalPosition);
				} catch (Exception e) {
					log.error("Error during processing: {}", e.getMessage(), e);
					log.info("Rolling back changes...");
					//TODO 입장알림 취소 롤백로직
					// 1. 원래 status로 돌려주기..
					// 2. 오토캔슬 큐에서 지우기
					// rollbackRushOperations(dto);

					throw new BobJoolException(ErrorCode.TRANSACTION_FAILED);
				} finally {
					if (lock.isHeldByCurrentThread()) {
						lock.unlock();
						log.info("Lock released for key: {}", lockKey);
					}
				}
			} else {
				log.warn("Failed to acquire lock for key: {}", lockKey);
				throw new BobJoolException(ErrorCode.FAILED_ACQUIRE_LOCK);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Thread interrupted while trying to acquire lock for key: {}", lockKey, e);
			throw new BobJoolException(ErrorCode.INTERRUPTED_WHILE_ACQUIRE_LOCK);
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

	private void addToWaitingQueue(QueueRegisterDto dto, long position) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(dto.restaurantId());
		double uniqueScore = (double)position;
		boolean addedToQueue = Boolean.TRUE.equals(
			redisTemplate.opsForZSet().add(waitingListKey, String.valueOf(dto.userId()), uniqueScore));
		if (!addedToQueue) {
			throw new BobJoolException(ErrorCode.FAILED_ADD_TO_QUEUE);
		}
	}

	private void saveUserInfoToRedis(QueueRegisterDto dto, long position) {
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(dto.restaurantId(), dto.userId());
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
		try {
			redisTemplate.opsForHash().putAll(userQueueDataKey, userInfo);
		} catch (Exception e) {
			log.error("Failed to save user info to Redis for key: {}", userQueueDataKey, e);
			throw new BobJoolException(ErrorCode.FAILED_SAVE);
		}
	}

	private void rollbackResisterOperations(QueueRegisterDto dto) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(dto.restaurantId());
		String userQueueDataKey = RedisKeyUtil.getUserQueueDataKey(dto.restaurantId(), dto.userId());
		String userIsWaitingKey = RedisKeyUtil.getUserIsWaitingKey(dto.userId());
		redisTemplate.opsForZSet().remove(waitingListKey, String.valueOf(dto.userId()));
		redisTemplate.delete(userQueueDataKey);
		redisTemplate.delete(userIsWaitingKey);
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

	public long getNextQueuePosition(UUID restaurantId, Long userId) {
		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Long size = redisTemplate.opsForZSet().size(waitingListKey);
		return Optional.ofNullable(size).orElse(0L) + 1;
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
				userQueueDataMap.getOrDefault("dining_option", "UNKNOWN").toString());
			QueueType type = QueueType.valueOf(userQueueDataMap.getOrDefault("type", "UNKNOWN").toString());
			QueueStatus status = QueueStatus.valueOf(userQueueDataMap.getOrDefault("status", "UNKNOWN").toString());
			long createdAt = Long.parseLong(userQueueDataMap.getOrDefault("created_at", "0").toString());
			LocalDateTime createdAtDateTime = null;
			if (createdAt > 0) {
				createdAtDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault());
			}
			Long delayCount = Long.valueOf(userQueueDataMap.getOrDefault("delay_count", "0").toString());

			return new UserQueueData(userId, restaurantId, position, member, diningOption, type, status, delayCount,
				createdAtDateTime);
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
