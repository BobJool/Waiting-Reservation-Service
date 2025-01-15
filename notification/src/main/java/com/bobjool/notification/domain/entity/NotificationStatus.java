package com.bobjool.notification.domain.entity;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationStatus {
    PENDING("전송 대기"),
    SENT("전송 완료"),
    FAILED("전송 실패")
    ;

    private final String description;

    public static NotificationStatus of(String request) {
        return switch (request.toUpperCase()){
            case "PENDING" -> PENDING;
            case "SENT" -> SENT;
            case "FAILED" -> FAILED;
            default -> throw new BobJoolException(ErrorCode.INVALID_NOTIFICATION_STATE);
        };
    }
}
