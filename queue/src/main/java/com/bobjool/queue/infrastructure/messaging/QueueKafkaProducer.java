package com.bobjool.queue.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.bobjool.queue.application.dto.kafka.QueueAlertedEvent;
import com.bobjool.queue.application.dto.kafka.QueueCanceledEvent;
import com.bobjool.queue.application.dto.kafka.QueueDelayedEvent;
import com.bobjool.queue.application.dto.kafka.QueueRegisteredEvent;
import com.bobjool.queue.application.dto.kafka.QueueRemindEvent;

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

	public void publishQueueDelayed(QueueDelayedEvent event) {
		publishKafka("queue.delayed", event);
	}

	public void publishQueueCanceled(QueueCanceledEvent event) {
		publishKafka("queue.canceled", event);
	}

	public void publishQueueRemind(QueueRemindEvent event) {
		publishKafka("queue.remind", event);
	}

	public void publishQueueAlerted(QueueAlertedEvent event) {
		publishKafka("queue.alerted", event);
	}

	public void publishQueueRush(QueueAlertedEvent event) {
		publishKafka("queue.rush", event);
	}
}
