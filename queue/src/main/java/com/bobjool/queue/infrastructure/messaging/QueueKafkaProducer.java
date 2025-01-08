package com.bobjool.queue.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.bobjool.queue.application.dto.QueueRegisteredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueKafkaProducer {
	private final KafkaTemplate<String, String> kafkaTemplate;

	public void publishKafka(String topic, Object event) {
		String eventToJson = EventSerializer.serialize(event);
		log.info("변환된 eventMessage : ,{}", eventToJson);
		kafkaTemplate.send(topic, eventToJson);
	}

	public void publishQueueRegistered(QueueRegisteredEvent event) {
		publishKafka("queue.registered", event);
	}
}
