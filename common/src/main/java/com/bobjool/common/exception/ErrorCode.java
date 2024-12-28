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

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
