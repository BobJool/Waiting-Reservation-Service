package com.bobjool.notification.domain.entity;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    SUCCESS("성공"),
    FAIL("실패"),
    CANCEL("취소"),
    RENEW("미루기"),
    RUSH("호출"),
    REMIND("리마인드"),
    REFUND("환불")
    ;

    private final String description;

    public static NotificationType of(String request) {
        return switch (request.toUpperCase()){
            case "SUCCESS" -> SUCCESS;
            case "FAIL" -> FAIL;
            case "CANCEL" -> CANCEL;
            case "RENEW" -> RENEW;
            case "RUSH" -> RUSH;
            case "REMIND" -> REMIND;
            case "REFUND" -> REFUND;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_NOTIFICATION_TYPE);
        };
    }
}
