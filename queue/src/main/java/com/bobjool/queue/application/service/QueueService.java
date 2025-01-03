package com.bobjool.queue.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.QueueRegisterDto;
import com.bobjool.queue.application.dto.QueueRegisterResDto;
import com.bobjool.queue.domain.entity.Queue;
import com.bobjool.queue.domain.repository.QueueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
	private final QueueRepository queueRepository;
	private final RedisQueueService redisQueueService;

	@Transactional
	public QueueRegisterResDto registerQueue(QueueRegisterDto request) {
		Long userId = request.userId();
		UUID restaurantId = request.restaurantId();

		if (redisQueueService.isUserAlreadyWaiting(userId)) {
			throw new BobJoolException(ErrorCode.USER_ALREADY_IN_QUEUE);
		}

		// double score = redisQueueService.getAllUsersInQueue(restaurantId).size() + 1;

		long rank = redisQueueService.addUserToQueue(restaurantId, userId.toString());
		redisQueueService.markUserAsWaiting(userId, restaurantId);

		// int position = queueRepository.findMaxPositionByRestaurantId(restaurantId) + 1;

		Queue queue = Queue.create(
			restaurantId,
			userId,
			request.member(),
			request.type(),
			request.diningOption(),
			queueRepository.findMaxPositionByRestaurantIdWithLock(restaurantId) + 1
		);

		log.info("registerQueue : {}", queue);
		return QueueRegisterResDto.from(queueRepository.save(queue), rank);
	}
}
