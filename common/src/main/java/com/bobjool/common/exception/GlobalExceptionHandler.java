package com.bobjool.common.exception;

import com.bobjool.common.presentation.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final String ERROR_LOG = "[ERROR] %s %s";

    @ExceptionHandler(BobJoolException.class)
    public ResponseEntity<ApiResponse<Void>> applicationException(final BobJoolException e){
        log.error(String.format(ERROR_LOG, e.getHttpStatus(), e.getMessage()));
        return ApiResponse.fail(e);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error(String.format(ERROR_LOG, e.getMessage(), e.getClass().getName()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST, "지원하지 않는 경로입니다."));
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> httpReqMethodNotSupportException(final HttpRequestMethodNotSupportedException e){
        log.error(String.format(ERROR_LOG, e.getMessage(), Arrays.toString(e.getSupportedMethods())));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청 방법입니다."));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> missingServletRequestParameter(final MissingServletRequestParameterException e) {
        log.error(String.format(ERROR_LOG, e.getParameterName(), e.getMessage()));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(HttpStatus.METHOD_NOT_ALLOWED, "필요한 파라미터가 입력되지 않았습니다."));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> methodArgumentNotValidException(final MethodArgumentNotValidException e){
        log.error(String.format(ERROR_LOG, e.getParameter(), e.getStatusCode()));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(HttpStatus.METHOD_NOT_ALLOWED, "요청하신 경로를 찾을 수 없습니다."));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> methodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e){
        log.error(String.format(ERROR_LOG, e.getParameter(), HttpStatus.BAD_REQUEST));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(HttpStatus.METHOD_NOT_ALLOWED, "해당 요청 방식은 지원되지 않습니다."));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(final BindException e) {
        log.error(String.format(ERROR_LOG, e.getObjectName(), e.getFieldErrors()));
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(HttpStatus.METHOD_NOT_ALLOWED, errorMessage));
    }
}
