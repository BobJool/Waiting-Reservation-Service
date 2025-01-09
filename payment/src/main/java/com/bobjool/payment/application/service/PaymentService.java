package com.bobjool.payment.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.payment.application.dto.PaymentCreateDto;
import com.bobjool.payment.application.dto.PaymentResDto;
import com.bobjool.payment.application.dto.PaymentSearchDto;
import com.bobjool.payment.application.dto.PaymentUpdateDto;
import com.bobjool.payment.application.events.PaymentCompletedEvent;
import com.bobjool.payment.application.events.PaymentFailedEvent;
import com.bobjool.payment.application.events.ReservationCreatedEvent;
import com.bobjool.payment.application.interfaces.PaymentProducer;
import com.bobjool.payment.application.interfaces.PgClient;
import com.bobjool.payment.domain.entity.Payment;
import com.bobjool.payment.domain.enums.PaymentMethod;
import com.bobjool.payment.domain.enums.PaymentStatus;
import com.bobjool.payment.domain.enums.PaymentTopic;
import com.bobjool.payment.domain.enums.PgName;
import com.bobjool.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PgClient pgClient;
    private final PaymentProducer paymentProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public PaymentResDto createPayment(PaymentCreateDto paymentCreateDto) {
        log.info("createPayment.PaymentCreateDto = {}", paymentCreateDto);
        PaymentStatus status = PaymentStatus.COMPLETE;

        // Redis에서 예약 정보 확인
        String redisKey = String.format("reservation:autoCancel:%s", paymentCreateDto.reservationId());

        ReservationCreatedEvent reservationEvent = (ReservationCreatedEvent) redisTemplate.opsForValue().get(redisKey);

        if (reservationEvent == null) {
            log.info("Reservation not found or expired in Redis for key: {}", redisKey);
            throw new BobJoolException(ErrorCode.EXPIRED_RESERVATION);
        }
        // Redis에서 키 제거
        redisTemplate.delete(redisKey);
        log.info("Found reservation in Redis: {}", reservationEvent);

        Payment payment = Payment.create(
                paymentCreateDto.reservationId(),
                paymentCreateDto.userId(),
                paymentCreateDto.amount(),
                PaymentMethod.of(paymentCreateDto.method()),
                PgName.of(paymentCreateDto.PgName())
        );
        // pg 사의 결제 요청

        if (!pgClient.requestPayment(payment)) {
            status = PaymentStatus.FAIL;
        }
        payment.updateStatus(status);
        paymentRepository.save(payment);

        // 카프카 토픽 발행
        // 결제 성공 상황
        if (status == PaymentStatus.COMPLETE) {
            PaymentCompletedEvent event = PaymentCompletedEvent.from(payment);
            paymentProducer.send(PaymentTopic.PAYMENT_COMPLETED.getTopic(), event);
        } else {
            // 결제 실패 상황
            PaymentFailedEvent event = PaymentFailedEvent.from(payment);
            paymentProducer.send(PaymentTopic.PAYMENT_FAILED.getTopic(), event);
        }

        return PaymentResDto.from(payment);
    }

    public Page<PaymentResDto> search(PaymentSearchDto paymentSearchDto, Pageable pageable) {
        log.info("search.PaymentSearchDto = {}, pageable = {}", paymentSearchDto, pageable);

        PaymentStatus status = null;
        if (paymentSearchDto.status() != null) {
            status = PaymentStatus.of(paymentSearchDto.status());
        }
        Page<Payment> paymentPage = paymentRepository.search(paymentSearchDto.userId(),
                status,
                paymentSearchDto.startDate(),
                paymentSearchDto.endDate(),
                pageable);
        return paymentPage.map(PaymentResDto::from);
    }

    @Transactional
    public PaymentResDto updatePaymentStatus(PaymentUpdateDto paymentUpdateDto, UUID paymentId) {
        log.info("updatePayment.PaymentUpdateDto = {}", paymentUpdateDto);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        payment.updateStatus(PaymentStatus.of(paymentUpdateDto.status()));
        return PaymentResDto.from(payment);
    }

    @Transactional
    public PaymentResDto refundPayment(UUID paymentId) {
        log.info("refundPayment.PaymentId = {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        payment.updateStatus(PaymentStatus.REFUND);
        return PaymentResDto.from(payment);
    }

    public PaymentResDto getPayment(UUID paymentId) {
        log.info("getPayment.PaymentId = {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        return PaymentResDto.from(payment);
    }
}
