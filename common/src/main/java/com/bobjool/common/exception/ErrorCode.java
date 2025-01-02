package com.bobjool.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 공통
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 엔티티가 존재하지 않습니다."),
    ALREADY_DELETED(HttpStatus.CONFLICT, "이미 삭제되었습니다."),
    UNSUPPORTED_SORT_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 정렬 조건입니다."),

    // 인증
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 존재하지 않습니다."),
    MISSING_ROLE(HttpStatus.BAD_REQUEST, "권한 정보가 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "유저 ID 또는 비밀번호 정보가 일치하지 않습니다."),

    // 레스토랑
    DUPLICATED_NAME(HttpStatus.BAD_REQUEST,"식당 이름이 이미 존재합니다."),
    DUPLICATED_PHONE(HttpStatus.BAD_REQUEST, "식당 번호가 이미 존재합니다."),
    DUPPLICATED_Address(HttpStatus.BAD_REQUEST,"식당 주소가 이미 존재합니다."),
    // 대기열

    // 예약

    // 결제
    UNSUPPORTED_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "지원하지 않는 결제 방식입니다."),
    UNSUPPORTED_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "지원하지 않는 결제 상태입니다."),
    UNSUPPORTED_PG_NAME(HttpStatus.BAD_REQUEST, "지원하지 않는 PG사 이름입니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액은 양수여야 합니다."),
    PAYMENT_FAIL(HttpStatus.SERVICE_UNAVAILABLE, "알 수 없는 이유로 결제에 실패했습니다."),
    CANNOT_REFUND(HttpStatus.CONFLICT, "환불할 수 없는 상태입니다."),

    // 알림

    // AI


    ;

    private final HttpStatus httpStatus;
    private final String message;
}
