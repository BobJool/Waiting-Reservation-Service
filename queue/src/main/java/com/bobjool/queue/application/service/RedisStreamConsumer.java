package com.bobjool.queue.application.service;

import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.queue.application.dto.redis.QueueAlertDto;
import com.bobjool.queue.application.dto.redis.QueueCancelDto;
import com.bobjool.queue.application.dto.redis.QueueCheckInDto;
import com.bobjool.queue.application.dto.redis.QueueDelayDto;
import com.bobjool.queue.application.dto.redis.QueueRegisterDto;
import com.bobjool.queue.infrastructure.messaging.EventSerializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {
	private final RedisTemplate<String, Object> redisTemplate;
	private final QueueService queueService;

	@Override
	public void onMessage(MapRecord<String, String, String> message) {

		Map<String, String> messageMap = message.getValue();
		String restaurantId = messageMap.get("groupId");

		String consumerGroup = "group-" + restaurantId;
		log.info("ConsumerGroup: " + consumerGroup);

		try {
			createConsumerGroupIfNotExists(message.getStream(), consumerGroup);
			processMessage(messageMap);
			redisTemplate.opsForStream().acknowledge(consumerGroup, message);
		} catch (Exception e) {
			log.warn("Error processing message: " + e.getMessage());
		}
	}


	private void createConsumerGroupIfNotExists(String streamKey, String groupName) {
		try {
			redisTemplate.opsForStream().createGroup(streamKey, groupName);
			log.info("Consumer group created: " + groupName);
		} catch (Exception e) {
			log.info("Consumer group already exists or failed to create: " + groupName);
		}
	}

	private void processMessage(Map<String, String> messageBody) throws Exception {
		String type = messageBody.get("type");
		String data = messageBody.get("data");

		log.info("Data: " + data);
		log.info("type: " + type);
		String sanitizedData = data.replace("\\\"", "\"").replaceAll("^\"|\"$", "");
		String sanitizedType = type.replace("\\\"", "\"").replaceAll("^\"|\"$", "");
		log.info("Sanitized Data: {}", sanitizedData);
		switch (sanitizedType) {
			case "register":
					QueueRegisterDto registerDto = EventSerializer.deserialize(sanitizedData, QueueRegisterDto.class);
					queueService.registerQueue(registerDto);
				break;

			case "delay":
				QueueDelayDto delayDto = EventSerializer.deserialize(sanitizedData, QueueDelayDto.class);
				log.info("delay에서 파싱하려고 한거야? 파싱한 dto: "+delayDto);
				queueService.delayUserRank(delayDto);
				break;

			case "cancel":
				QueueCancelDto cancelDto = EventSerializer.deserialize(sanitizedData, QueueCancelDto.class);
				queueService.cancelWaiting(cancelDto);
				break;

			case "checkin":
				QueueCheckInDto checkInDto = EventSerializer.deserialize(sanitizedData, QueueCheckInDto.class);
				queueService.checkInRestaurant(checkInDto);
				break;

			case "alert":
				QueueAlertDto alertDto = EventSerializer.deserialize(sanitizedData, QueueAlertDto.class);
				queueService.sendAlertNotification(alertDto);
				break;

			case "rush":
				QueueAlertDto remindDto = EventSerializer.deserialize(sanitizedData, QueueAlertDto.class);
				queueService.sendRushAlertNotification(remindDto);
				break;

			default:
				throw new BobJoolException(ErrorCode.UNKNOWN_TOPIC);
		}
	}
}

