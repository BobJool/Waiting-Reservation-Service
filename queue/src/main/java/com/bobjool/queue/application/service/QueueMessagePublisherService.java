package com.bobjool.queue.application.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.QueueCancelDto;
import com.bobjool.queue.application.dto.QueueCheckInDto;
import com.bobjool.queue.application.dto.QueueDelayDto;
import com.bobjool.queue.application.dto.QueueRegisterDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueueMessagePublisherService {

	private final StringRedisTemplate stringRedisTemplate;
	private final ObjectMapper objectMapper;
	private final ChannelTopic registerTopic;
	private final ChannelTopic delayTopic;
	private final ChannelTopic cancelTopic;
	private final ChannelTopic checkInTopic;

	public String publishRegisterQueue(QueueRegisterDto dto) {
		return publishMessage(registerTopic.getTopic(), dto, "대기열 요청 성공");
	}

	public String publishDelayQueue(QueueDelayDto dto) {
		return publishMessage(delayTopic.getTopic(), dto, "대기열 내 순서 미루기 요청 성공");
	}

	public String publishCancelQueue(QueueCancelDto dto) {
		return publishMessage(cancelTopic.getTopic(), dto, "대기열 줄서기 취소 요청 성공");
	}

	public String publishCheckInQueue(QueueCheckInDto dto) {
		return publishMessage(checkInTopic.getTopic(), dto, "대기열 식당 체크인 요청 성공");
	}

	private String publishMessage(String topic, Object dto, String successMessage) {
		try {
			String message = objectMapper.writeValueAsString(dto);
			publishMessage(topic, message);
			return successMessage;
		} catch (Exception e) {
			throw new BobJoolException(ErrorCode.QUEUE_PUBLISHING_FAILED);
		}
	}

	public void publishMessage(String topic, String message) {
		stringRedisTemplate.convertAndSend(topic, message);
	}
}
