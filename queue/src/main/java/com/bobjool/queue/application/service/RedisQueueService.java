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
			throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
		}
		return rank + 1;
	}

	public List<String> getNextTenUsersWithOrder(UUID restaurantId, Long userId) {
		String redisKey = "queue:restaurant:" + restaurantId + ":usersList";
		long userRank = getUserPositionInQueue(restaurantId, userId);

		Set<Object> nextUsers = redisTemplate.opsForZSet().range(redisKey, userRank, userRank + 9);
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
		String redisKey = "queue:restaurant:" + restaurantId + ":usersList";
		String userHashKey = "queue:restaurant:" + restaurantId + ":user:" + userId;

		// 해시테이블에서 추가 정보 조회
		Long originalPosition = (Long) redisTemplate.opsForHash().get(userHashKey, "position");
		Integer member = (Integer) redisTemplate.opsForHash().get(userHashKey, "member");

		if (originalPosition == null || member == null) {
			throw new IllegalArgumentException("대기번호 또는 방문인원 정보가 없습니다.");
		}

		// 대상 유저의 score 조회
		Double targetScore = getUserScore(redisKey, targetUserId);

		// 현재 유저의 score 조회
		Double userScore = getUserScore(redisKey, userId);

		// 이미 대상 사용자 뒤에 있는 경우 처리하지 않음
		if (userScore > targetScore) {
			throw new IllegalArgumentException("이미 대상 사용자 뒤에 있습니다.");
		}

		// 대상 유저 다음 유저의 score 조회
		Set<Object> nextUsers = redisTemplate.opsForZSet().rangeByScore(redisKey, targetScore, Double.MAX_VALUE, 1, 1);
		Double nextScore = nextUsers.isEmpty()
			? targetScore + 1 // 다음 유저가 없으면 targetScore + 1로 설정
			: getUserScore(redisKey, Long.parseLong(nextUsers.iterator().next().toString()));

		if (nextScore == null) {
			nextScore = targetScore + 1; // 다음 유저가 없는 경우 기본값 설정
		}

		// 새로운 score를 중간값으로 설정
		double newScore = (targetScore + nextScore) / 2.0;
		redisTemplate.opsForZSet().add(redisKey, String.valueOf(userId), newScore);

		// 새로운 순번 계산
		Long newRank = getUserPositionInQueue(restaurantId, userId);

		log.info("Delayed user {} to queue {} with newRank {}", userId, redisKey, newRank);

		// DTO 생성 및 반환
		return new QueueDelayResDto(newRank + 1, originalPosition, member);
	}

	void validateAndUpdateDelayCount(String userHashKey) {
		Integer delayCount = (Integer) redisTemplate.opsForHash().get(userHashKey, "delay_count");
		if (delayCount == null) delayCount = 0;

		if (delayCount >= 2) {
			throw new IllegalArgumentException("순서 미루기는 최대 2번까지 가능합니다.");
		}
		redisTemplate.opsForHash().put(userHashKey, "delay_count", delayCount + 1);
	}

	private Double getUserScore(String redisKey, Long userId) {
		Double score = redisTemplate.opsForZSet().score(redisKey, String.valueOf(userId));
		if (score == null) {
			throw new IllegalArgumentException("유저가 존재하지 않습니다: userId=" + userId);
		}
		return score;
	}
}
