package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.application.dto.payment.PaymentCreateDto;
import com.bobjool.reservation.application.dto.payment.PaymentResDto;
import com.bobjool.reservation.application.interfaces.PgClient;
import com.bobjool.reservation.domain.entity.Payment;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.PaymentMethod;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import com.bobjool.reservation.domain.enums.PgName;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import com.bobjool.reservation.domain.repository.PaymentRepository;
import com.bobjool.reservation.domain.repository.ReservationRepository;
import com.bobjool.reservation.domain.service.ReservationPaymentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationPaymentService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationPaymentDomainService reservationPaymentDomainService;
    private final PgClient pgClient;

    @Transactional
    public PaymentResDto createPayment(PaymentCreateDto paymentCreateDto) {
        log.info("createPayment.PaymentCreateDto = {}", paymentCreateDto);
        Reservation reservation = reservationRepository.findById(paymentCreateDto.reservationId())
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        // PENDING 이 아닌 예약은 예외 발생
        if(reservation.isNotPending()) {
            throw new BobJoolException(ErrorCode.PAYMENT_FAIL);
        }

        Payment payment = Payment.create(
                paymentCreateDto.reservationId(),
                paymentCreateDto.userId(),
                paymentCreateDto.amount(),
                PaymentMethod.of(paymentCreateDto.method()),
                PgName.of(paymentCreateDto.PgName())
        );
        // pg 사의 결제 요청
        PaymentStatus status = PaymentStatus.COMPLETE;
        if (!pgClient.requestPayment(payment)) {
            status = PaymentStatus.FAIL;
        }
        payment.updateStatus(status);

        // 결제 성공 -> 예약 성공 / 결제 실패 -> 예약 실패

        reservationPaymentDomainService.syncReservationWithPayment(reservation, payment);

        return PaymentResDto.from(paymentRepository.save(payment));
    }
}
