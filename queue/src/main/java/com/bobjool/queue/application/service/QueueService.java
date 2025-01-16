package com.bobjool.queue.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.queue.application.dto.QueueStatusResDto;
import com.bobjool.queue.application.dto.RestaurantCheckOwnerReqDto;
import com.bobjool.queue.application.dto.RestaurantCheckValidReqDto;
import com.bobjool.queue.application.dto.RestaurantValidResDto;
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
import com.bobjool.queue.application.client.RestaurantClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final RedisQueueService redisQueueService;
	private final RestaurantClient restaurantClient;
	private final RedisStreamProducer redisStreamProducer;
	private final QueueKafkaProducer queueKafkaProducer;

	public String publishStreams(UUID restaurantId, Long userId, Object dto, String processType) {
		boolean isUserWaiting = redisQueueService.isUserWaiting(userId);
		boolean isUserInQueue = redisQueueService.isUserInQueue(restaurantId, userId);

		switch (processType.toLowerCase()) {
			case "register" -> {
				if (!isUserWaiting) {
					redisStreamProducer.produceMessage(restaurantId, dto, processType);
				} else {
					throw new BobJoolException(ErrorCode.USER_ALREADY_IN_QUEUE);
				}
			}
			case "delay" -> {
				if (isUserWaiting && isUserInQueue) {
					QueueDelayDto delayDto = (QueueDelayDto) dto;
					redisQueueService.validateNotLastInQueue(delayDto.restaurantId(), delayDto.userId());
					redisQueueService.validateDelayCount(delayDto.restaurantId(), delayDto.userId());
					redisStreamProducer.produceMessage(restaurantId, dto, processType);
				} else {
					throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
				}
			}
			case "cancel", "checkin", "alert", "rush" -> {
				if (isUserInQueue) {
					redisStreamProducer.produceMessage(restaurantId, dto, processType);
				} else {
					throw new BobJoolException(ErrorCode.USER_NOT_FOUND_IN_QUEUE);
				}
			}
			default -> throw new BobJoolException(ErrorCode.INVALID_PROCESS_TYPE);
		}
		return "사용자 요청이 등록되었습니다.";
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
		QueueDelayedEvent event = redisQueueService.delayUserRank(dto);
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

	public void validateRestaurant(UUID restaurantId) {
		RestaurantCheckValidReqDto reqDto = RestaurantCheckValidReqDto.from(restaurantId);
		ApiResponse<RestaurantValidResDto> response = restaurantClient.restaurantValidCheck(reqDto);

		if (!response.data().isQueue() && response.data().isDeleted()) {
			throw new BobJoolException(ErrorCode.NOT_AVAILABLE_RESTAURANT);
		}
	}

	public void isExistRestaurant(UUID restaurantId) {
		RestaurantCheckValidReqDto reqDto = RestaurantCheckValidReqDto.from(restaurantId);
		ApiResponse<RestaurantValidResDto> response = restaurantClient.restaurantValidCheck(reqDto);
		log.info("식당존재검사 : dto- "+reqDto+ "response.data().isDeleted()"+response.data().isDeleted());
		if(response.data().isDeleted()) {
			throw new BobJoolException(ErrorCode.NOT_AVAILABLE_RESTAURANT);
		}
	}

	public void isRestaurantOwner(Long userId, UUID restaurantId) {
		RestaurantCheckOwnerReqDto reqDto = RestaurantCheckOwnerReqDto.from(userId, restaurantId);

		if (!restaurantClient.restaurantOwnerCheck(reqDto)) {
			throw new BobJoolException(ErrorCode.NOT_OWNER);
		}
	}
}
