package com.bobjool.notification.domain.entity;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationChannel {
    SLACK("슬랙"),
    EMAIL("이메일")
    ;

    private final String description;

    public static NotificationChannel of(String request) {
        return switch (request.toUpperCase()){
            case "SLACK" -> SLACK;
            case "EMAIL" -> EMAIL;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_CHANNEL_TYPE);
        };
    }
}
