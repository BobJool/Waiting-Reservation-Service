package com.bobjool.queue.application.service;

import java.util.UUID;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.QueueCancelDto;
import com.bobjool.queue.domain.enums.CancelType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisExpirationListener implements MessageListener {
	private final QueueService queueService;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();

		// 만료된 키가 autoCancel 키스페이스인지 확인
		if (expiredKey.startsWith("queue:autoCancel:")) {
			try {
				String[] parts = expiredKey.split(":");
				if (parts.length != 4) {
					throw new IllegalArgumentException("Invalid key format: " + expiredKey);
				}
				UUID restaurantId = UUID.fromString(parts[2]);
				Long userId = Long.parseLong(parts[3]);

				QueueCancelDto cancelDto = new QueueCancelDto(restaurantId, userId, CancelType.SYSTEM.getDescription());
				log.info("redis 키 만료 이벤트 수신하여 자동 줄서기 취소 실행");
				queueService.cancelWaiting(cancelDto);
			} catch (Exception e) {
				throw new BobJoolException(ErrorCode.FAILED_PARSE_MESSAGE);
			}
		}
	}
}
