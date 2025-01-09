package com.bobjool.queue.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.QueueStatusResDto;
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
import com.bobjool.queue.infrastructure.messaging.QueueKafkaProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final RedisQueueService redisQueueService;
	private final QueueMessagePublisher queuePublisherService;
	private final QueueKafkaProducer queueKafkaProducer;

	public String handleQueue(UUID restaurantId, Long userId, Object dto, String processType) {
		boolean isUserWaiting = redisQueueService.isUserWaiting(userId);
		boolean isUserInQueue = redisQueueService.isUserInQueue(restaurantId, userId);
		return switch (processType.toLowerCase()) {
			case "register" -> {
				if (!isUserWaiting) {
					yield queuePublisherService.publishRegisterQueue((QueueRegisterDto)dto);
				} else {
					throw new BobJoolException(ErrorCode.USER_ALREADY_IN_QUEUE);
				}
			}
			case "delay" -> {
				if (isUserInQueue) {
					QueueDelayDto delayDto = (QueueDelayDto)dto;
					redisQueueService.validateNotLastInQueue(delayDto.restaurantId(), delayDto.userId());
					redisQueueService.validateDelayCount(delayDto.restaurantId(), delayDto.userId());
					yield queuePublisherService.publishDelayQueue(delayDto);
				} else {
					throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
				}
			}
			case "cancel" -> {
				if (isUserInQueue) {
					yield queuePublisherService.publishCancelQueue((QueueCancelDto)dto);
				} else {
					throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
				}
			}
			case "checkin" -> {
				if (isUserInQueue) {
					yield queuePublisherService.publishCheckInQueue((QueueCheckInDto)dto);
				} else {
					throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
				}
			}
			case "alert" -> {
				if (isUserInQueue) {
					redisQueueService.checkUserStatus(restaurantId, userId, "alert");
					yield queuePublisherService.publishAlertQueue((QueueAlertDto)dto);
				} else {
					throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
				}
			}
			case "rush" -> {
				if (isUserInQueue) {
					redisQueueService.checkUserStatus(restaurantId, userId, "rush");
					yield queuePublisherService.publishRushQueue((QueueAlertDto)dto);
				} else {
					throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
				}
			}
			default -> throw new BobJoolException(ErrorCode.INVALID_PROCESS_TYPE);
		};
	}

	public void registerQueue(QueueRegisterDto dto) {
		QueueRegisteredEvent event = redisQueueService.registerQueue(dto);
		queueKafkaProducer.publishQueueRegistered(event);
	}

	public QueueStatusResDto getNextTenUsersWithOrder(UUID restaurantId, Long userId) {
		if (redisQueueService.isUserWaiting(userId)) {
			long rank = redisQueueService.getUserIndexInQueue(restaurantId, userId) + 1;
			List<String> nextUsers = redisQueueService.getNextTenUsersWithOrder(restaurantId, userId);
			return new QueueStatusResDto(rank, nextUsers);
		} else {
			throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
		}
	}

	public void delayUserRank(QueueDelayDto dto) {
		QueueDelayedEvent event = redisQueueService.delayUserRank(dto.restaurantId(), dto.userId(), dto.targetUserId());
		queueKafkaProducer.publishQueueDelayed(event);
	}

	public void cancelWaiting(QueueCancelDto dto) {
		QueueCanceledEvent event = redisQueueService.cancelWaiting(dto);
		queueKafkaProducer.publishQueueCanceled(event);
	}

	public void checkInRestaurant(QueueCheckInDto dto) {
		QueueRemindEvent event = redisQueueService.checkInRestaurant(dto);
		queueKafkaProducer.publishQueueRemind(event);
	}

	public void sendAlertNotification(QueueAlertDto dto) {
		QueueAlertedEvent event = redisQueueService.sendAlertNotification(dto);
		queueKafkaProducer.publishQueueAlerted(event);
	}

	public void sendRushAlertNotification(QueueAlertDto dto) {
		QueueAlertedEvent event = redisQueueService.sendRushAlertNotification(dto);
		queueKafkaProducer.publishQueueRush(event);
	}
}
