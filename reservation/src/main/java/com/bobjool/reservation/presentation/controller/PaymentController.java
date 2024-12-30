package com.bobjool.reservation.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.PageResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.reservation.application.dto.PaymentResponse;
import com.bobjool.reservation.application.dto.PaymentSearchDto;
import com.bobjool.reservation.application.service.PaymentService;
import com.bobjool.reservation.presentation.dto.PaymentCreateReqDto;
import com.bobjool.reservation.presentation.dto.PaymentUpdateReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> search(@RequestParam(value = "userId", required = false) Long userId,
                                                                          @RequestParam(value = "status", required = false) String status,
                                                                          @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                                                          @RequestParam(value = "endDate", required = false) LocalDate endDate,
                                                                          @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                          Pageable pageable){
        Page<PaymentResponse> responsePage
                = paymentService.search(new PaymentSearchDto(userId, status, startDate, endDate), pageable);
        return ApiResponse.success(SuccessCode.SUCCESS, PageResponse.of(responsePage));
    }

    @PatchMapping("/status/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> updatePaymentStatus(@Valid @RequestBody PaymentUpdateReqDto paymentUpdateReqDto,
                                    @PathVariable("paymentId") UUID paymentId) {
        PaymentResponse response = paymentService.updatePaymentStatus(paymentUpdateReqDto.toServiceDto(), paymentId);
        return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
    }


}
