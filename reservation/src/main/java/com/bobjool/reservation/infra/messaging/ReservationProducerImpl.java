package com.bobjool.reservation.infra.messaging;


import com.bobjool.reservation.application.events.ReservationCompletedEvent;
import com.bobjool.reservation.application.events.ReservationCreatedEvent;
import com.bobjool.reservation.application.events.ReservationFailedEvent;
import com.bobjool.reservation.application.interfaces.ReservationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationProducerImpl implements ReservationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishReservationCreated(String topic, ReservationCreatedEvent event) {
        String eventToJson = EventSerializer.serialize(event);
        kafkaTemplate.send(topic, eventToJson);
        log.info("ReservationProducer Sent ReservationCreatedEvent = {}", eventToJson);
    }

    public void publishReservationCompleted(String topic, ReservationCompletedEvent event) {
        String eventToJson = EventSerializer.serialize(event);
        kafkaTemplate.send(topic, eventToJson);
        log.info("ReservationProducer Sent ReservationCompletedEvent = {}", eventToJson);
    }

    public void publishReservationFailed(String topic, ReservationFailedEvent event) {
        String eventToJson = EventSerializer.serialize(event);
        kafkaTemplate.send(topic, eventToJson);
        log.info("ReservationProducer Sent ReservationFailedEvent = {}", eventToJson);
    }
}
