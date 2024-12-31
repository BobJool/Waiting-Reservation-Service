package com.bobjool.reservation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    PENDING("예약 대기"),
    COMPLETE("예약 대기"),
    CHECK_IN("예약 대기"),
    CANCEL("예약 대기"),
    NO_SHOW("예약 대기"),
    ;

    private final String description;
}
