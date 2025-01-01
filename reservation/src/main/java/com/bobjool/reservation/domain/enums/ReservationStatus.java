package com.bobjool.reservation.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    PENDING("예약 대기"),
    COMPLETE("예약 완료"),
    CHECK_IN("체크인"),
    CANCEL("예약 취소"),
    NO_SHOW("노쇼"), // "예약 부도" 라는 말이 있던데, 너무 생소해서 그냥 "노쇼"로 하겠습니다.
    ;

    private final String description;

    public static ReservationStatus of(String request) {
        return switch (request) {
            case "PENDING" -> PENDING;
            case "COMPLETE" -> COMPLETE;
            case "CHECK_IN" -> CHECK_IN;
            case "CANCEL" -> CANCEL;
            case "NO_SHOW" -> NO_SHOW;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_RESERVATION_STATUS);
        };
    }
}
