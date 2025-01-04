package com.bobjool.queue.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.queue.application.dto.QueueDelayResDto;
import com.bobjool.queue.application.dto.QueueStatusResDto;
import com.bobjool.queue.application.service.QueueService;
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
		@Valid @RequestBody QueueRegisterReqDto queueRegisterReqDto) {
		return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED,
			queueService.publishRegisterQueue(queueRegisterReqDto.toServiceDto()));
	}

	@GetMapping("/queues/{restaurantId}/{userId}")
	public ResponseEntity<ApiResponse<QueueStatusResDto>> getNextTenUsersWithOrder(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId) {
		return ApiResponse.success(SuccessCode.SUCCESS,
			queueService.getNextTenUsersWithOrder(restaurantId, userId));
	}

	@PostMapping("/queues/{restaurantId}/{userId}")
	public ResponseEntity<ApiResponse<QueueDelayResDto>> delayUserRank(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId,
		@RequestParam Long targetUserId) {
		// TODO : 롤검증 /오너라면 자신식당의 웨이팅정보변경인지 검증/ 손님이라면 내 줄서기 정보인지 확인
		return ApiResponse.success(SuccessCode.SUCCESS, queueService.delayUserRank(restaurantId,userId,targetUserId));
	}

}
