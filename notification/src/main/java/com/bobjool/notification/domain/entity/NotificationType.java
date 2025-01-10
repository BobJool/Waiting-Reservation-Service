package com.bobjool.notification.domain.entity;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    COMPLETED("예약 완료"),
    FAILED("예약 실패"),
    REFUND("예약 환불"),
    REGISTERED("웨이팅 등록"),
    DELAYED("웨이팅 줄미루기"),
    CANCELED("웨이팅 취소"),
    ALERTED("웨이팅 입장"),
    RUSH("웨이팅 호출"),
    REMIND("리마인드")
    ;

    private final String description;

    public static NotificationType of(String request) {
        return switch (request.toUpperCase()){
            case "COMPLETED" -> COMPLETED;
            case "FAILED" -> FAILED;
            case "REFUND" -> REFUND;
            case "REGISTERED" -> REGISTERED;
            case "DELAYED" -> DELAYED;
            case "CANCELED" -> CANCELED;
            case "ALERTED" -> ALERTED;
            case "RUSH" -> RUSH;
            case "REMIND" -> REMIND;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_NOTIFICATION_TYPE);
        };
    }
}
