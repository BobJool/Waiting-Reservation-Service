package com.bobjool.notification.infrastructure.config.circuit;

import com.bobjool.notification.infrastructure.messaging.KafkaManager;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j(topic = "CircuitBreakerConsumerConfiguration")
@Configuration
public class CircuitBreakerConsumerConfiguration {
    public CircuitBreakerConsumerConfiguration(CircuitBreakerRegistry circuitBreakerRegistry, KafkaManager kafkaManager) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("pushNotificationToSlack");

        circuitBreaker.getEventPublisher().onStateTransition(event -> {

            CircuitBreaker.StateTransition transition = event.getStateTransition();
            log.info("CircuitBreaker has changed status: {}", transition);

            switch (transition) {
                case CLOSED_TO_OPEN:
                case CLOSED_TO_FORCED_OPEN:
                case HALF_OPEN_TO_OPEN:
                    kafkaManager.pause();
                    break;
                case OPEN_TO_HALF_OPEN:
                case HALF_OPEN_TO_CLOSED:
                case FORCED_OPEN_TO_CLOSED:
                case FORCED_OPEN_TO_HALF_OPEN:
                    kafkaManager.resume();
                    break;
                default:
                    log.warn("CircuitBreaker changed to unknown state: {}", transition);
            }
        });
    }
}
