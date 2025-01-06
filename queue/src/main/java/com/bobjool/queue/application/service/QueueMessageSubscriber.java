package com.bobjool.queue.application.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.bobjool.queue.application.dto.QueueCancelDto;
import com.bobjool.queue.application.dto.QueueCheckInDto;
import com.bobjool.queue.application.dto.QueueDelayDto;
import com.bobjool.queue.application.dto.QueueRegisterDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueueMessageSubscriber implements MessageListener {
	private final QueueService queueService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String topic = new String(pattern);
		String messageBody = new String(message.getBody());

		switch (topic) {
			case "queue.register":
				QueueRegisterDto registerDto = parseMessage(messageBody, QueueRegisterDto.class);
				queueService.registerQueue(registerDto);
				break;

			case "queue.delay":
				QueueDelayDto delayDto = parseMessage(messageBody, QueueDelayDto.class);
				queueService.delayUserRank(delayDto);
				break;

			case "queue.cancel":
				QueueCancelDto cancelDto = parseMessage(messageBody, QueueCancelDto.class);
				queueService.cancelWaiting(cancelDto);
				break;

			case "queue.checkin":
				QueueCheckInDto checkInDto = parseMessage(messageBody, QueueCheckInDto.class);
				queueService.checkInRestaurant(checkInDto);
				break;

			default:
				throw new IllegalArgumentException("Unknown topic: " + topic);
		}
	}

	private <T> T parseMessage(String messageBody, Class<T> valueType) {
		try {
			return objectMapper.readValue(messageBody, valueType);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse message body: " + messageBody, e);
		}
	}

}
