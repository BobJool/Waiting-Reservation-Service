package com.bobjool.restaurant.application.exception;

import com.bobjool.common.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestaurantExceptionHandler extends GlobalExceptionHandler {

}