package com.bobjool.payment.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.payment.application.dto.PaymentCreateDto;
import com.bobjool.payment.application.dto.PaymentResDto;
import com.bobjool.payment.application.dto.PaymentSearchDto;
import com.bobjool.payment.application.dto.PaymentUpdateDto;
import com.bobjool.payment.domain.entity.Payment;
import com.bobjool.payment.domain.enums.PaymentStatus;
import com.bobjool.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResDto createPayment(PaymentCreateDto paymentCreateDto) {
        log.info("createPayment.PaymentCreateDto = {}", paymentCreateDto);

        // todo 레디스 확인하여 10분 이내에 저장된 예약인지 확인
        // 결제 성공시 payment.completed 토픽에 이벤트 발행
        // 결제 실패시 payment.failed 토픽에 이벤트 발행
        return null;
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
