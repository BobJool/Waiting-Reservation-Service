package com.bobjool.reservation.domain.service;

import com.bobjool.reservation.domain.entity.Payment;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import org.springframework.stereotype.Service;

/**
 * 도메인 서비스 도입!
 * 참고 자료: DDD 특강(남동현 튜터님)
 * https://github.com/Kkaekkae/ddd_lecture_source/blob/main/src/main/java/com/sparta/ddd/domain/service/UserProductDomainService.java
 * */
@Service
public class ReservationPaymentDomainService {

    /**
     * Payment 가 성공하면 Reservation 도 성공,
     * Payment 가 실패하면 Reservation 도 실패
     */
    public void syncReservationWithPayment(Reservation reservation, Payment payment) {
        ReservationStatus updatedStatus = switch (payment.getStatus()) {
            case FAIL -> ReservationStatus.FAIL;
            case COMPLETE -> ReservationStatus.COMPLETE;
            default -> ReservationStatus.PENDING;
        };
        reservation.updateStatus(updatedStatus);
    }
}
