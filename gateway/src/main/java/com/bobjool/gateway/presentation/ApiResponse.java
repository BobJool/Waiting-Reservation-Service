package com.bobjool.gateway.presentation;

import com.bobjool.gateway.exception.TokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T> (
        HttpStatus status,
        String message,
        T data
) {
    public static <T> ResponseEntity<ApiResponse<T>> success(SuccessCode successCode) {
        return ResponseEntity.status(successCode.getHttpStatus())
                .body(new ApiResponse<>(successCode.getHttpStatus(), successCode.getMessage(), null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(SuccessCode successCode, T data) {
        return ResponseEntity.status(successCode.getHttpStatus())
                .body(new ApiResponse<>(successCode.getHttpStatus(), successCode.getMessage(), data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(TokenException e) {
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ApiResponse<>(e.getHttpStatus(), e.getMessage(), null));
    }

    public static <T> ApiResponse<T> fail(HttpStatus status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
