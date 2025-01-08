package com.bobjool.payment.infra.messaging;

import com.bobjool.payment.application.events.PaymentCompletedEvent;
import com.bobjool.payment.application.events.PaymentFailedEvent;
import com.bobjool.payment.application.interfaces.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProducerImpl implements PaymentProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 결제 완료(성공) 이벤트 발행: payment.completed 토픽
     * */
    public void send(String topic, PaymentCompletedEvent event){
        String eventToJson = EventSerializer.serialize(event);
        kafkaTemplate.send(topic, eventToJson);
        log.info("PaymentProducer Sent PaymentCompletedEvent = {}", eventToJson);
    }

    /**
     * 결제 실패 이벤트 발행: payment.failed 토픽
     * */
    public void send(String topic, PaymentFailedEvent event){
        String eventToJson = EventSerializer.serialize(event);
        kafkaTemplate.send(topic, eventToJson);
        log.info("PaymentProducer Sent PaymentFailedEvent = {}", eventToJson);
    }
}
