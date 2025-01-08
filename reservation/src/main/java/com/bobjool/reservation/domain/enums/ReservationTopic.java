package com.bobjool.reservation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 토픽 이름이 많아지면 enum 으로 관리한다.
 * */
@Getter
@AllArgsConstructor
public enum ReservationTopic {

    // 예약 요청(생성) 이벤트 (reservation-service 가 발행)
    RESERVATION_CREATED("reservation.created"),

    // 예약 완료(성공) 이벤트 (reservation-service 가 발행)
    RESERVATION_COMPLETED("reservation.completed"),

    // 예약 실패 이벤트 (reservation-service 가 발행)
    RESERVATION_FAILED("reservation.failed"),

    // 결제 완료(성공) 이벤트 (payment-service 가 발행)
    PAYMENT_COMPLETED("payment.completed"),

    // 결제 실패 이벤트 (payment-service 가 발행)
    PAYMENT_FAILED("payment.failed"),


    ;
    private final String topic;
}
