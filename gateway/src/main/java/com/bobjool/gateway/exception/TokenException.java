package com.bobjool.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public TokenException(ErrorCode errorCode) {
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }
}
