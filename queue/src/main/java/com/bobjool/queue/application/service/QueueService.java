package com.bobjool.queue.application.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.QueueCancelDto;
import com.bobjool.queue.application.dto.QueueDelayDto;
import com.bobjool.queue.application.dto.QueueDelayResDto;
import com.bobjool.queue.application.dto.QueueRegisterDto;
import com.bobjool.queue.application.dto.QueueStatusResDto;
import com.bobjool.queue.domain.util.RedisKeyUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final RedisQueueService redisQueueService;
	private final QueueMessagePublisherService queuePublisherService;

	public String handleQueue(Object dto, String processType) {
		return switch (processType.toLowerCase()) {
			case "register" -> queuePublisherService.publishRegisterQueue((QueueRegisterDto)dto);
			case "delay" -> queuePublisherService.publishDelayQueue((QueueDelayDto)dto);
			case "cancel" -> queuePublisherService.publishCancelQueue((QueueCancelDto)dto);
			default -> throw new BobJoolException(ErrorCode.INVALID_PROCESS_TYPE);
		};
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
		long rank = redisQueueService.getUserIndexInQueue(restaurantId, userId) + 1;
		//TODO 1: restaurant service : 식당이름 가져오기
		//TODO 2: auth service : 슬랙ID(혹은 이메일)
		//TODO: 카프카 메세지 발행 > queue.registered
	}

	public QueueStatusResDto getNextTenUsersWithOrder(UUID restaurantId, Long userId) {
		long rank = redisQueueService.getUserIndexInQueue(restaurantId, userId) + 1;
		List<String> nextUsers = redisQueueService.getNextTenUsersWithOrder(restaurantId, userId);
		return new QueueStatusResDto(rank, nextUsers);
	}

	public void delayUserRank(QueueDelayDto dto) {
		String userHashKey = RedisKeyUtil.getUserQueueDataKey(dto.restaurantId(), dto.userId());
		redisQueueService.validateNotLastInQueue(dto.restaurantId(), dto.userId());
		redisQueueService.validateDelayCount(userHashKey);
		QueueDelayResDto response = redisQueueService.delayUserRank(dto.restaurantId(), dto.userId(), dto.targetUserId());
		//TODO 1: restaurant service : 식당이름 가져오기
		//TODO 2: auth service : 슬랙ID(혹은 이메일)
		//TODO 3: 카프카 메세지 발행 > queue.delayed
	}

	public void cancelWaiting(QueueCancelDto cancelDto) {
	}
}
