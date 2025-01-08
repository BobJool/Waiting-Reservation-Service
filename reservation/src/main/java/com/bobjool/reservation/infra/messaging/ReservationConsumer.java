package com.bobjool.reservation.infra.messaging;

import com.bobjool.reservation.application.events.PaymentCompletedEvent;
import com.bobjool.reservation.application.events.PaymentFailedEvent;
import com.bobjool.reservation.application.events.PaymentTimeoutEvent;
import com.bobjool.reservation.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationConsumer {
    private final ReservationService reservationService;

    /**
     * 결제 완료(성공)에 대한 이벤트 구독, 처리
     */
    @KafkaListener(topics = "payment.completed")
    public void listenPaymentCompleted(String kafkaMessage) {
        log.info("ReservatioConsumer.listenPaymentCompleted.kafkaMessage = {}", kafkaMessage);

        PaymentCompletedEvent paymentCompletedEvent
                = EventSerializer.deserialize(kafkaMessage, PaymentCompletedEvent.class);
        reservationService.updateReservationCompleted(paymentCompletedEvent);
    }

    /**
     * 결제 실패에 대한 이벤트 구독, 처리
     * */
    @KafkaListener(topics = "payment.failed")
    public void listenPaymentFailed(String kafkaMessage) {
        log.info("ReservatioConsumer.listenPaymentFailed.kafkaMessage = {}", kafkaMessage);

        PaymentFailedEvent paymentFailedEvent
                = EventSerializer.deserialize(kafkaMessage, PaymentFailedEvent.class);
        reservationService.updateReservationFailed(paymentFailedEvent);
    }

    /**
     * 결제 시간 초과에 대한 이벤트 구독, 처리
     * */
    @KafkaListener(topics = "payment.timeout")
    public void listenPaymentTimeout(String kafkaMessage) {
        log.info("ReservatioConsumer.listenPaymentTimeout.kafkaMessage = {}", kafkaMessage);

        PaymentTimeoutEvent paymentTimeoutEvent
                = EventSerializer.deserialize(kafkaMessage, PaymentTimeoutEvent.class);
        reservationService.updateReservationFailed(paymentTimeoutEvent);
    }
}
