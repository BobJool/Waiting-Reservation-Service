package com.bobjool.reservation.infra.kafka;


import com.bobjool.reservation.EventSerializer;
import com.bobjool.reservation.application.events.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(String topic, ReservationCreatedEvent event) {
        String eventToJson = EventSerializer.serialize(event);
        kafkaTemplate.send(topic, eventToJson);
        log.info("ReservationProducer Sent ReservationCreatedEvent = {}", eventToJson);
    }
}
