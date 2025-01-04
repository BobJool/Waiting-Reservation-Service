package com.bobjool.queue.application.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

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
		String messageBody = new String(message.getBody());
		QueueRegisterDto dto = parseMessage(messageBody);
		queueService.registerQueue(dto);
	}

	private QueueRegisterDto parseMessage(String messageBody) {
		try {
			return objectMapper.readValue(messageBody, QueueRegisterDto.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse message body: " + messageBody, e);
		}
	}
}
