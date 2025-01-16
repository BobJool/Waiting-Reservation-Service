package com.bobjool.queue.application.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.infrastructure.messaging.EventSerializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamProducer {
	private final RedisTemplate<String, Object> redisTemplate;

	public void produceMessage(UUID restaurantId, Object dto, String processType) {
		try {
			String streamKey = "queue-stream";
			log.info("ProduceMessage StreamKey: {}", streamKey);
			Map<String, Object> message = new HashMap<>();
			message.put("type", processType);
			message.put("data", EventSerializer.serialize(dto));
			message.put("groupId", String.valueOf(restaurantId));
			redisTemplate.opsForStream().add(streamKey, message);
		} catch (Exception e) {
			throw new BobJoolException(ErrorCode.QUEUE_PUBLISHING_FAILED);
		}
	}
}