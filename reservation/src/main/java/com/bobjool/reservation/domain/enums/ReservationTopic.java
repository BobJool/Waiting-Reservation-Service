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

    ;
    private final String topic;
}
