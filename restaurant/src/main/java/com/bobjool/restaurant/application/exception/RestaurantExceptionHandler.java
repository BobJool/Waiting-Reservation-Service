package com.bobjool.restaurant.application.exception;

import com.bobjool.common.exception.GlobalExceptionHandler;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantCategory;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestaurantExceptionHandler extends GlobalExceptionHandler {

  private final String ERROR_LOG = "[ERROR] %s %s";

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> httpMessageNotReadableException(final HttpMessageNotReadableException e) {
    Throwable cause = e.getCause();

    // 특정 예외인지 확인
    if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException invalidFormatException) {
      Class<?> targetType = invalidFormatException.getTargetType();
      // RestaurantCategory Enum인지 확인
      if (RestaurantCategory.class.equals(targetType)) {
        // RestaurantCategory 처리 시 발생한 문제인 경우
        log.error("[ERROR] 잘못된 Category 값입니다. {}", invalidFormatException.getValue());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "잘못된 Category 값입니다."));
      }
      //RestaurantRegion Enum인지 확인
      if (RestaurantRegion.class.equals(targetType)) {
        log.error("[ERROR] 잘못된 Region 값입니다. {}", invalidFormatException.getValue());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "잘못된 Region 값입니다."));
      }
    }
    // 기본 처리
    log.error(String.format(ERROR_LOG, e.getMessage(), e.getClass().getName()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "잘못된 JSON 형식입니다."));
  }



}