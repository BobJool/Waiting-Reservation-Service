package com.bobjool.notification.application.dto;

import org.springframework.http.HttpStatus;

public record ApiResponseDto<T>(
        HttpStatus status,
        String message,
        T data
) {
}
