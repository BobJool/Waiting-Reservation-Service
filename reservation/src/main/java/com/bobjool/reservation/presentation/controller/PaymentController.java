package com.bobjool.reservation.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.reservation.application.dto.PaymentResponse;
import com.bobjool.reservation.application.service.PaymentService;
import com.bobjool.reservation.presentation.dto.PaymentCreateReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@RestController
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createProduct(@Valid @RequestBody PaymentCreateReqDto paymentCreateReqDto) {
        log.info("createProduct.paymentCreateReqDto={}", paymentCreateReqDto);
        PaymentResponse response = paymentService.createPayment(paymentCreateReqDto.toServiceDto());
        return ApiResponse.success(SuccessCode.SUCCESS_INSERT, response);
    }

}
