package com.bobjool.queue.application.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.QueueDelayResDto;
import com.bobjool.queue.application.dto.QueueRegisterDto;
import com.bobjool.queue.application.dto.QueueStatusResDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final RedisQueueService redisQueueService;
	private final ChannelTopic registerTopic;

	public String publishRegisterQueue(QueueRegisterDto dto) {
		try {
			redisQueueService.publishMessage(registerTopic.getTopic(), dto.toString());
			return "대기열 요청 성공";
		} catch (Exception e) {
			throw new BobJoolException(ErrorCode.QUEUE_PUBLISHING_FAILED);
		}
	}
	@Transactional
	public void registerQueue(QueueRegisterDto request) {
		Long userId = request.userId();
		UUID restaurantId = request.restaurantId();

		if (redisQueueService.isUserAlreadyWaiting(userId)) {
			throw new BobJoolException(ErrorCode.USER_ALREADY_IN_QUEUE);
		}

		Map<String, Object> userInfo = redisQueueService.addUserToQueue(request);
		redisQueueService.markUserAsWaiting(userId, restaurantId);
		long rank = redisQueueService.getUserPositionInQueue(restaurantId, userId);

		//TODO: 카프카 메세지 발행 > queue.registered
	}

	public QueueStatusResDto getNextTenUsersWithOrder(UUID restaurantId, Long userId) {
		long rank = redisQueueService.getUserPositionInQueue(restaurantId, userId);
		List<String> nextUsers = redisQueueService.getNextTenUsersWithOrder(restaurantId, userId);
		return new QueueStatusResDto(rank, nextUsers);
	}

	public QueueDelayResDto delayUserRank(UUID restaurantId, Long userId, Long targetUserId) {
		String userHashKey = "queue:restaurant:" + restaurantId + ":user:" + userId;
		redisQueueService.validateNotLastInQueue(restaurantId,userId);
		redisQueueService.validateDelayCount(userHashKey);
		QueueDelayResDto response = redisQueueService.delayUserRank(restaurantId, userId, targetUserId);
		//TODO: 카프카 메세지 발행 > queue.delayed
		return response;
	}
}
