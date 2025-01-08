package com.bobjool.queue.application.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.redis.QueueRegisterDto;
import com.bobjool.queue.application.dto.redis.QueueAlertDto;
import com.bobjool.queue.application.dto.redis.QueueCancelDto;
import com.bobjool.queue.application.dto.redis.QueueCheckInDto;
import com.bobjool.queue.application.dto.redis.QueueDelayDto;
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

			case "queue.alert":
				QueueAlertDto alertDto = parseMessage(messageBody, QueueAlertDto.class);
				queueService.sendAlertNotification(alertDto);
				break;

			case "queue.rush":
				QueueAlertDto remindDto = parseMessage(messageBody, QueueAlertDto.class);
				queueService.sendRushAlertNotification(remindDto);
				break;

			default:
				throw new BobJoolException(ErrorCode.UNKNOWN_TOPIC);
		}
	}

	private <T> T parseMessage(String messageBody, Class<T> valueType) {
		try {
			return objectMapper.readValue(messageBody, valueType);
		} catch (Exception e) {
			throw new BobJoolException(ErrorCode.FAILED_PARSE_MESSAGE);
		}
	}

}
