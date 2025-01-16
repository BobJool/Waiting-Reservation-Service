package com.bobjool.payment.infra.redis;

import com.bobjool.payment.application.events.PaymentTimeoutEvent;
import com.bobjool.payment.domain.enums.PaymentTopic;
import com.bobjool.payment.infra.messaging.EventSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisExpirationListener implements MessageListener {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        // 만료된 키가 reservation 키스페이스인지 확인
        if (expiredKey.startsWith("reservation:autoCancel")) {
            try {
                // 키에서 reservationId 추출
                String[] parts = expiredKey.split(":");
                if (parts.length != 3) { // "payment", "autoCancel", "{reservationId}"로 나뉘어야 함
                    throw new IllegalArgumentException("Invalid key format: " + expiredKey);
                }
                String reservationId = parts[2];

                // Kafka에 payment.timeout 이벤트 발행
                PaymentTimeoutEvent paymentTimeoutEvent = new PaymentTimeoutEvent(UUID.fromString(reservationId));
                String eventToJson = EventSerializer.serialize(paymentTimeoutEvent);
                kafkaTemplate.send(PaymentTopic.PAYMENT_TIMEOUT.getTopic(), eventToJson);
                log.info("Redis 키 만료 이벤트 수신: reservationId={}, payment.timeout 이벤트 발행", reservationId);
            } catch (Exception e) {
                log.error("Failed to process expired key: {}", expiredKey, e);
            }
        }
    }
}
