package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.application.dto.payment.PaymentCreateDto;
import com.bobjool.reservation.application.dto.payment.PaymentResDto;
import com.bobjool.reservation.application.dto.payment.PaymentSearchDto;
import com.bobjool.reservation.application.dto.payment.PaymentUpdateDto;
import com.bobjool.reservation.application.interfaces.PgClient;
import com.bobjool.reservation.domain.entity.Payment;
import com.bobjool.reservation.domain.enums.PaymentMethod;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import com.bobjool.reservation.domain.enums.PgName;
import com.bobjool.reservation.domain.repository.PaymentRepository;
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
