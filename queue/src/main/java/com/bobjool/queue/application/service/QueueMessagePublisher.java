package com.bobjool.queue.application.service;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.redis.QueueAlertDto;
import com.bobjool.queue.application.dto.redis.QueueCancelDto;
import com.bobjool.queue.application.dto.redis.QueueCheckInDto;
import com.bobjool.queue.application.dto.redis.QueueDelayDto;
import com.bobjool.queue.application.dto.redis.QueueRegisterDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueueMessagePublisher {

	private final StringRedisTemplate stringRedisTemplate;
	private final ObjectMapper objectMapper;
	private final Map<String, ChannelTopic> channelTopics;

	public String publishRegisterQueue(QueueRegisterDto dto) {
		return publishMessage("register", dto, "대기열 요청 성공");
	}

	public String publishDelayQueue(QueueDelayDto dto) {
		return publishMessage("delay", dto, "대기열 내 순서 미루기 요청 성공");
	}

	public String publishCancelQueue(QueueCancelDto dto) {
		return publishMessage("cancel", dto, "대기열 줄서기 취소 요청 성공");
	}

	public String publishCheckInQueue(QueueCheckInDto dto) {
		return publishMessage("checkIn", dto, "대기열 식당 체크인 요청 성공");
	}

	public String publishAlertQueue(QueueAlertDto dto) {
		return publishMessage("alert", dto, "대기열 사용자 식당 입장 알림 요청 성공");
	}

	public String publishRushQueue(QueueAlertDto dto) {
		return publishMessage("rush", dto, "대기열 사용자 식당 입장요청 알림 요청 성공");
	}

	private String publishMessage(String topicKey, Object dto, String successMessage) {
		try {
			String topic = channelTopics.get(topicKey).getTopic();
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
