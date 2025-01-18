package com.bobjool.queue.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bobjool.common.infra.aspect.RequireRole;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.queue.application.dto.QueueStatusResDto;
import com.bobjool.queue.application.dto.redis.QueueAlertDto;
import com.bobjool.queue.application.dto.redis.QueueCancelDto;
import com.bobjool.queue.application.dto.redis.QueueCheckInDto;
import com.bobjool.queue.application.dto.redis.QueueDelayDto;
import com.bobjool.queue.application.service.QueueService;
import com.bobjool.queue.domain.enums.CancelType;
import com.bobjool.queue.presentation.dto.QueueRegisterReqDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class QueueController {

	private final QueueService queueService;

	@RequireRole(value = {"CUSTOMER", "OWNER", "MASTER"})
	@PostMapping("/queues")
	public ResponseEntity<ApiResponse<String>> registerQueue(
		@Valid @RequestBody QueueRegisterReqDto request) {
		queueService.validateRestaurant(request.restaurantId());
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.publishStreams(request.restaurantId(), request.userId(), request.toServiceDto(), "register"));
	}

	@RequireRole(value = {"CUSTOMER", "OWNER", "MASTER"})
	@GetMapping("/queues/{restaurantId}/{userId}")
	public ResponseEntity<ApiResponse<QueueStatusResDto>> getNextTenUsersWithOrder(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId) {
		queueService.isExistRestaurant(restaurantId);
		return ApiResponse.success(SuccessCode.SUCCESS,
			queueService.getNextTenUsersWithOrder(restaurantId, userId));
	}

	@RequireRole(value = {"CUSTOMER", "OWNER", "MASTER"})
	@PostMapping("/queues/{restaurantId}/{userId}")
	public ResponseEntity<ApiResponse<String>> delayUserRank(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId,
		@RequestParam Long targetUserId,
		@RequestHeader(value = "X-User-Id", required = true) String headerUerId,
		@RequestHeader(value = "X-Role", required = true) String role
	) {
		queueService.isExistRestaurant(restaurantId);
		if (role.equals("OWNER")) {
			queueService.isRestaurantOwner(Long.parseLong(headerUerId), restaurantId);
		}
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.publishStreams(restaurantId, userId,
				new QueueDelayDto(restaurantId, userId, targetUserId), "delay"));
	}

	@RequireRole(value = {"CUSTOMER", "OWNER", "MASTER"})
	@DeleteMapping("/queues/{restaurantId}/{userId}")
	public ResponseEntity<ApiResponse<String>> cancelQueue(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId,
		@RequestHeader(value = "X-User-Id", required = true) String headerUerId,
		@RequestHeader(value = "X-Role", required = true) String role
	) {
		queueService.isExistRestaurant(restaurantId);
		if (role.equals("OWNER")) {
			queueService.isRestaurantOwner(Long.parseLong(headerUerId), restaurantId);
		}
		CancelType cancelType = CancelType.fromRole(role);
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.publishStreams(restaurantId, userId,
				new QueueCancelDto(restaurantId, userId, cancelType), "cancel"));
	}

	@RequireRole(value = {"OWNER", "MASTER"})
	@PostMapping("/queues/{restaurantId}/{userId}/check-in")
	public ResponseEntity<ApiResponse<String>> checkInRestaurant(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId,
		@RequestHeader(value = "X-User-Id", required = true) String headerUerId,
		@RequestHeader(value = "X-Role", required = true) String role
	) {
		queueService.isExistRestaurant(restaurantId);
		if (role.equals("OWNER")) {
			queueService.isRestaurantOwner(Long.parseLong(headerUerId), restaurantId);
		}
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.publishStreams(restaurantId, userId, new QueueCheckInDto(restaurantId, userId), "checkin"));
	}

	@RequireRole(value = {"OWNER", "MASTER"})
	@PostMapping("/queues/{restaurantId}/{userId}/alert")
	public ResponseEntity<ApiResponse<String>> sendAlertNotification(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId,
		@RequestHeader(value = "X-User-Id", required = true) String headerUerId,
		@RequestHeader(value = "X-Role", required = true) String role
	) {
		queueService.isExistRestaurant(restaurantId);
		if (role.equals("OWNER")) {
			log.info("식당오너 검증 오너id : {}", headerUerId);
			queueService.isRestaurantOwner(Long.parseLong(headerUerId), restaurantId);
		}
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.publishStreams(restaurantId, userId, new QueueAlertDto(restaurantId, userId), "alert"));
	}

	@RequireRole(value = {"OWNER", "MASTER"})
	@PostMapping("/queues/{restaurantId}/{userId}/rush")
	public ResponseEntity<ApiResponse<String>> sendRushNotification(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId,
		@RequestHeader(value = "X-User-Id", required = true) String headerUerId,
		@RequestHeader(value = "X-Role", required = true) String role
	) {
		queueService.isExistRestaurant(restaurantId);
		if (role.equals("OWNER")) {
			queueService.isRestaurantOwner(Long.parseLong(headerUerId), restaurantId);
		}
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.publishStreams(restaurantId, userId, new QueueAlertDto(restaurantId, userId), "rush"));
	}

}
