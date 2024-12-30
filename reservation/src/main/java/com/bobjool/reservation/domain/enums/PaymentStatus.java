package com.bobjool.reservation.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    PENDING("결제 대기"),
    COMPLETE("결제 완료"),
    FAIL("결제 실패"),
    REFUND("환불"),

    ;

    /**
     * enum 에 설명 필드를 꼭 넣는 편입니다. 주석을 대체하는 느낌으로요!
     * */
    private final String description;

    public static PaymentStatus of(String request) {
        return switch (request) {
            case "PENDING" -> PENDING;
            case "COMPLETE" -> COMPLETE;
            case "FAIL" -> FAIL;
            case "REFUND" -> REFUND;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_PAYMENT_STATUS);
        };
    }
}
