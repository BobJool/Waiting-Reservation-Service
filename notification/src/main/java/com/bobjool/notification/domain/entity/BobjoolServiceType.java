package com.bobjool.notification.domain.entity;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BobjoolServiceType {
    QUEUE("웨이팅"),
    RESERVATION("예약")
    ;

    private final String description;

    public static BobjoolServiceType of(String request) {
        return switch (request.toUpperCase()){
            case "QUEUE" -> QUEUE;
            case "RESERVATION" -> RESERVATION;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_SERVICE_TYPE);
        };
    }
}
