package com.bobjool.notification.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j(topic = "KafkaManager")
@Component
public class KafkaManager {
    private final KafkaListenerEndpointRegistry registry;

    @Autowired
    public KafkaManager(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    public void pause() {
        log.info("Pause all Kafka listener containers");
        registry.getListenerContainers().forEach(MessageListenerContainer::pause);
    }

    public void resume() {
        log.info("Resume all Kafka listener containers");
        registry.getListenerContainers().forEach(MessageListenerContainer::resume);
    }
}
