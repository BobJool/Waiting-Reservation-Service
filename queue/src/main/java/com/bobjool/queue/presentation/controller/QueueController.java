package com.bobjool.queue.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
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
			queueService.publishToQueue(queueRegisterReqDto.toServiceDto()));
	}

	@GetMapping("/queues/{restaurantId}/{userId}")
	public ResponseEntity<ApiResponse<QueueStatusResDto>> getNextTenUsersWithOrder(
		@PathVariable UUID restaurantId,
		@PathVariable Long userId) {
		return ApiResponse.success(SuccessCode.SUCCESS,
			queueService.getNextTenUsersWithOrder(restaurantId, userId));
	}

}
