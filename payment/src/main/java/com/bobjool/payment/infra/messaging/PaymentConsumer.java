package com.bobjool.payment.infra.messaging;

import com.bobjool.payment.application.service.PaymentService;
import com.bobjool.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;
    /**
     * 예약 요청(생성)에 대한 이벤트 구독(수신), 처리
     * 레디스에 저장
     * */
    @KafkaListener(topics = "reservation.created")
    public void processReservation() {
        // todo 레디스에 저장하는 역할 수행
    }
}
