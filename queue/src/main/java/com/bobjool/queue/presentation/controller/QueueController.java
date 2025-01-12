package com.bobjool.queue.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.queue.application.dto.QueueStatusResDto;
import com.bobjool.queue.application.dto.kafka.QueueRegisteredEvent;
import com.bobjool.queue.application.dto.redis.QueueAlertDto;
import com.bobjool.queue.application.dto.redis.QueueCancelDto;
import com.bobjool.queue.application.dto.redis.QueueCheckInDto;
import com.bobjool.queue.application.dto.redis.QueueDelayDto;
import com.bobjool.queue.application.service.QueueService;
import com.bobjool.queue.domain.enums.CancelType;
import com.bobjool.queue.presentation.dto.QueueRegisterReqDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class QueueController {

	private final QueueService queueService;

	@PostMapping("/queues")
	public ResponseEntity<ApiResponse<String>> registerQueue(
		@Valid @RequestBody QueueRegisterReqDto request) {
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.handleQueue(request.restaurantId(), request.userId(), request.toServiceDto(), "register"));
	}
	// @PostMapping("/queues")
	// public ResponseEntity<ApiResponse<QueueRegisteredEvent>> registerQueue(
	// 	@Valid @RequestBody QueueRegisterReqDto request) {
	// 	return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
	// 		queueService.registerQueue(request.toServiceDto()));
	// }

	@GetMapping("/queues/{restaurantId}/{userId}")
	public ResponseEntity<ApiResponse<QueueStatusResDto>> getNextTenUsersWithOrder(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId) {
		return ApiResponse.success(SuccessCode.SUCCESS,
			queueService.getNextTenUsersWithOrder(restaurantId, userId));
	}

	@PostMapping("/queues/{restaurantId}/{userId}")
	public ResponseEntity<ApiResponse<String>> delayUserRank(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId,
		@RequestParam Long targetUserId) {
		// TODO : 롤검증 /오너라면 자신식당의 웨이팅정보변경인지 검증/ 손님이라면 내 줄서기 정보인지 확인
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.handleQueue(restaurantId, userId, new QueueDelayDto(restaurantId, userId, targetUserId),
				"delay"));
	}

	@DeleteMapping("/queues/{restaurantId}/{userId}")
	public ResponseEntity<ApiResponse<String>> cancelQueue(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId) {
		// TODO : 롤검증 /오너라면 자신식당의 웨이팅정보변경인지 검증/ 손님이라면 내 줄서기 정보인지 확인
		// TODO : 분기 /오너나 관리자라면 QueueCancelDto.reason : owner_or_admin / QueueCancelDto.reason : customer
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.handleQueue(restaurantId, userId,
				new QueueCancelDto(restaurantId, userId, CancelType.COSTUMER),
				"cancel"));
	}

	@PostMapping("/queues/{restaurantId}/{userId}/check-in")
	public ResponseEntity<ApiResponse<String>> checkInRestaurant(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId) {
		// TODO : 롤검증 /오너
		// TODO 자신식당인지 검증
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.handleQueue(restaurantId, userId, new QueueCheckInDto(restaurantId, userId), "checkin"));
	}

	@PostMapping("/queues/{restaurantId}/{userId}/alert")
	public ResponseEntity<ApiResponse<String>> sendAlertNotification(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId) {
		// TODO: 롤 검증 / 오너
		// TODO: 자신 식당인지 검증
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.handleQueue(restaurantId, userId, new QueueAlertDto(restaurantId, userId), "alert"));
	}

	@PostMapping("/queues/{restaurantId}/{userId}/rush")
	public ResponseEntity<ApiResponse<String>> sendRushNotification(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId) {
		// TODO: 롤 검증 / 오너
		// TODO: 자신 식당인지 검증
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.handleQueue(restaurantId, userId, new QueueAlertDto(restaurantId, userId), "rush"));
	}

}
