package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.application.dto.PaymentCreateDto;
import com.bobjool.reservation.application.dto.PaymentResponse;
import com.bobjool.reservation.application.interfaces.PgClient;
import com.bobjool.reservation.domain.entity.Payment;
import com.bobjool.reservation.domain.enums.PaymentMethod;
import com.bobjool.reservation.domain.enums.PgName;
import com.bobjool.reservation.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PgClient pgClient;

    /**
     * 예외 발생 가능 한 곳은 테스트 합니다.
     * 1. PaymentMethod.of()
     * 2. PgName.of()
     * 3. pgClient.requestPayment()
     * 4. amount 가 음수 또는 0 일때 - 이거는 컨트롤러에서 검증 하니까 해도 되고 안 해도 되긴 합니다. 하는 게 좋긴 하겠죠?
     * */
    @Transactional
    public PaymentResponse createPayment(PaymentCreateDto paymentCreateDto) {
        log.info("createPayment.PaymentCreateDto = {}", paymentCreateDto);

        Payment payment = Payment.create(
                paymentCreateDto.reservationId(),
                paymentCreateDto.userId(),
                paymentCreateDto.amount(),
                PaymentMethod.of(paymentCreateDto.method()),
                PgName.of(paymentCreateDto.PgName())
        );
        // pg 사의 결제 요청
        if (!pgClient.requestPayment(payment)) {
            throw new BobJoolException(ErrorCode.PAYMENT_FAIL);
        }
        return PaymentResponse.from(paymentRepository.save(payment));
    }
}
