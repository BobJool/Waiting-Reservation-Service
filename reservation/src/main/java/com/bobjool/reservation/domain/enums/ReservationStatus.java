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
    FAIL("예약 실패"),
    ;

    private final String description;

    public static ReservationStatus of(String request) {
        return switch (request) {
            case "PENDING" -> PENDING;
            case "COMPLETE" -> COMPLETE;
            case "CHECK_IN" -> CHECK_IN;
            case "CANCEL" -> CANCEL;
            case "NO_SHOW" -> NO_SHOW;
            case "FAIL" -> FAIL;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_RESERVATION_STATUS);
        };
    }

    /**
     * 예약 상태가 취소 가능한지 확인
     * CHECK_IN, NO_SHOW 상태에서는 취소 불가
     */
    public boolean canCancel() {
        return this != CHECK_IN && this != NO_SHOW && this != FAIL;
    }

    /**
     * 읽기 좋은 코드를 위해 !canCancel 은 canNotCancel 로 감싸서 클라이언트에게 제공합니다.
     * */
    public boolean canNotCancel() {
        return !canCancel();
    }
}
