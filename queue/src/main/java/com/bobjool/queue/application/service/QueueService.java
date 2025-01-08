package com.bobjool.queue.application.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.QueueAlertDto;
import com.bobjool.queue.application.dto.QueueCancelDto;
import com.bobjool.queue.application.dto.QueueCheckInDto;
import com.bobjool.queue.application.dto.QueueDelayDto;
import com.bobjool.queue.application.dto.QueueDelayResDto;
import com.bobjool.queue.application.dto.QueueRegisterDto;
import com.bobjool.queue.application.dto.QueueRegisteredEvent;
import com.bobjool.queue.application.dto.QueueStatusResDto;
import com.bobjool.queue.infrastructure.messaging.QueueKafkaProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final RedisQueueService redisQueueService;
	private final QueueMessagePublisherService queuePublisherService;
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

	@Transactional
	public void registerQueue(QueueRegisterDto request) {
		Long userId = request.userId();
		UUID restaurantId = request.restaurantId();

		Map<String, Object> userInfo = redisQueueService.addUserToQueue(request);
		redisQueueService.markUserAsWaiting(userId, restaurantId);
		long rank = redisQueueService.getUserIndexInQueue(restaurantId, userId) + 1;
		//TODO 변경: 카프카 메세지 발행(유저ID, 식당ID, 대기인원, 대기순번, 대기번호) > queue.registered
		queueKafkaProducer.publishQueueRegistered(QueueRegisteredEvent.from(userId, restaurantId, userInfo, rank));
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
		QueueDelayResDto response = redisQueueService.delayUserRank(dto.restaurantId(), dto.userId(),
			dto.targetUserId());
		//TODO 1: restaurant service : restaurant_name 가져오기
		//TODO 2: auth service : 슬랙ID
		//TODO 3: 카프카 메세지 발행(슬랙ID, 식당명, 대기인원, 바뀐대기순번, 대기번호) > queue.delayed
		//TODO 변경: 카프카 메세지 발행(유저ID, 식당ID, 대기인원member, 바뀐대기순번rank, 대기번호position) > queue.delayed
	}

	public void cancelWaiting(QueueCancelDto cancelDto) {
		redisQueueService.cancelWaiting(cancelDto);
		//TODO 1: restaurant service : 식당이름 가져오기
		//TODO 2: auth service : 슬랙ID, 사용자명
		//TODO 3: 카프카 메세지 발행(슬랙ID, 식당명, 사용자명, 취소사유(reason, 문장만들어서) > queue.canceled
		//TODO 변경: 카프카 메세지 발행(유저ID, 식당ID, 취소사유(reason, 문장만들어서) > queue.canceled
	}

	public void checkInRestaurant(QueueCheckInDto checkInDto) {
		redisQueueService.checkInRestaurant(checkInDto);
		// 체크인 다음 3번째 유저 정보(순번 3, 대기 번호) 가져오기
		//TODO 1: restaurant service : 식당이름 가져오기
		//TODO 2: auth service : 슬랙ID, 사용자명
		//TODO 3: 카프카 메세지 발행(슬랙ID, 식당명, 사용자명, 순번(3), 대기번호) > queue.remind
		//TODO 변경: 카프카 메세지 발행(유저ID, 식당ID, 순번(3), 대기번호) > queue.remind

	}

	public void sendAlertNotification(QueueAlertDto dto) {
		Integer position = redisQueueService.sendAlertNotification(dto);
		log.info("sendAlertNotification() position : " + position);
		//TODO 1: restaurant service : 식당이름 가져오기
		//TODO 2: auth service : 슬랙ID, 사용자명
		//TODO 3: 카프카 메세지 발행(슬랙ID, 식당명, 사용자명, 대기번호) > queue.queue.alerted
		//TODO 변경: 카프카 메세지 발행(유저ID, 식당ID, 대기번호) > queue.queue.alerted
	}

	public void sendRushAlertNotification(QueueAlertDto dto) {
		Integer position = redisQueueService.sendRushAlertNotification(dto);
		log.info("sendRushAlertNotification() 실행");
		log.info("sendRushAlertNotification() 유저의 대기번호 : " + position);
		//TODO 1: restaurant service : 식당이름 가져오기
		//TODO 2: auth service : 슬랙ID
		//TODO 3: 카프카 메세지 발행(슬랙ID, 식당명, 대기번호) > queue.rush
		//TODO 변경:: 카프카 메세지 발행(유저ID, 식당ID, 대기번호) > queue.rush
	}
}
