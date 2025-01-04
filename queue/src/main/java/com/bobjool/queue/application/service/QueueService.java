package com.bobjool.queue.application.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.QueueRegisterDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final ChannelTopic topic;
	private final RedisQueueService redisQueueService;

	public String publishToQueue(QueueRegisterDto dto) {
		try {
			redisQueueService.publishMessage(topic.getTopic(), dto.toString());
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

	}
}
