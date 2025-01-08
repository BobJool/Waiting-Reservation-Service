package com.bobjool.reservation.application.exception;

import com.bobjool.common.exception.GlobalExceptionHandler;
import com.bobjool.common.presentation.ApiResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ReservationExceptionHandler extends GlobalExceptionHandler {

    private final String ERROR_LOG = "[ERROR] %s %s";

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignException(final FeignException e) {
        log.error(String.format(ERROR_LOG, e.status(), e.getMessage()));

        // 상태 코드와 메시지를 기반으로 적절한 응답 생성
        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 상태 코드가 null인 경우 기본값 설정
        }

        String errorMessage = "외부 서비스 호출 중 오류가 발생했습니다.";
        if (status == HttpStatus.CONFLICT) {
            errorMessage = "해당 예약이 만료되어 예약이 불가능합니다."; // 409 상태 코드에 대한 커스텀 메시지
        }

        return ResponseEntity.status(status)
                .body(ApiResponse.fail(status, errorMessage));
    }
}
