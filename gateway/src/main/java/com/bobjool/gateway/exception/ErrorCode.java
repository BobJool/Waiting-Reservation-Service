package com.bobjool.gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "유효하지 않은 Authorization 헤더 형식입니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "요청에 토큰이 없습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_BLACKLISTED(HttpStatus.FORBIDDEN, "해당 토큰은 블랙리스트에 등록되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
    // 동적으로 메시지를 생성하는 메서드
    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }
}