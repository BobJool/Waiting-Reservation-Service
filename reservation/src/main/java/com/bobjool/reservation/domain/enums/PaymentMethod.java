package com.bobjool.reservation.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("카드"),
    CASH("현금"),

    ;
    private final String description;

    public static PaymentMethod of(String request) {
        return switch (request) {
            case "CARD" -> CARD;
            case "CASH" -> CASH;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_PAYMENT_METHOD);
        };
    }
}
