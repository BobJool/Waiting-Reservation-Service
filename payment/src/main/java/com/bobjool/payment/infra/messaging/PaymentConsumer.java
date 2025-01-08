package com.bobjool.payment.infra.messaging;

import com.bobjool.payment.application.events.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 예약 요청(생성)에 대한 이벤트 구독(수신), 처리
     * 레디스에 저장
     */
    @KafkaListener(topics = "reservation.created")
    public void listen(String kafkaMessage) {
        log.info("PaymentConsumer.listen.kafkaMessage = {}", kafkaMessage);
        ReservationCreatedEvent event = EventSerializer.deserialize(kafkaMessage, ReservationCreatedEvent.class);

        // Redis 키 생성 (예: reservation:autoCancel:{reservationId})
        String redisKey = String.format("reservation:autoCancel:%s", event.reservationId());

        // Redis에 데이터 저장 및 TTL 설정 (10분)
        redisTemplate.opsForValue().set(redisKey, event, 1, TimeUnit.MINUTES);

        log.info("Saved reservation to Redis with key: {}", redisKey);
    }
}
