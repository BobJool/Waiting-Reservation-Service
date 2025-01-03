package com.bobjool.queue.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.queue.application.dto.QueueRegisterResDto;
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
	public ResponseEntity<ApiResponse<QueueRegisterResDto>> registerQueue(
		@Valid @RequestBody QueueRegisterReqDto queueRegisterReqDto) {
		return ApiResponse.success(SuccessCode.SUCCESS_INSERT,
			queueService.registerQueue(queueRegisterReqDto.toServiceDto()));
	}

}
