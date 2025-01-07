package com.bobjool.payment.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.PageResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.payment.application.dto.PaymentResDto;
import com.bobjool.payment.application.dto.PaymentSearchDto;
import com.bobjool.payment.application.service.PaymentService;
import com.bobjool.payment.presentation.dto.PaymentCreateReqDto;
import com.bobjool.payment.presentation.dto.PaymentUpdateReqDto;
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
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v2/payments")
@RestController
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResDto>> createPayment(@Valid @RequestBody PaymentCreateReqDto paymentCreateReqDto) {
        log.info("createProduct.paymentCreateReqDto={}", paymentCreateReqDto);
        PaymentResDto response = paymentService.createPayment(paymentCreateReqDto.toServiceDto());
        return ApiResponse.success(SuccessCode.SUCCESS_INSERT, response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PaymentResDto>>> search(@RequestParam(value = "userId", required = false) Long userId,
                                                                           @RequestParam(value = "status", required = false) String status,
                                                                           @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                                                           @RequestParam(value = "endDate", required = false) LocalDate endDate,
                                                                           @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                           Pageable pageable){
        Page<PaymentResDto> responsePage
                = paymentService.search(new PaymentSearchDto(userId, status, startDate, endDate), pageable);
        return ApiResponse.success(SuccessCode.SUCCESS, PageResponse.of(responsePage));
    }

    @PatchMapping("/status/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResDto>> updatePaymentStatus(@Valid @RequestBody PaymentUpdateReqDto paymentUpdateReqDto,
                                                                          @PathVariable("paymentId") UUID paymentId) {
        PaymentResDto response = paymentService.updatePaymentStatus(paymentUpdateReqDto.toServiceDto(), paymentId);
        return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
    }

    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResDto>> refundPayment(@PathVariable("paymentId") UUID paymentId) {
        log.info("refundPayment.paymentId={}", paymentId);
        PaymentResDto response = paymentService.refundPayment(paymentId);
        return ApiResponse.success(SuccessCode.SUCCESS, response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResDto>> getPayment(@PathVariable("paymentId") UUID paymentId) {
        log.info("getPayment.paymentId={}", paymentId);
        PaymentResDto response = paymentService.getPayment(paymentId);
        return ApiResponse.success(SuccessCode.SUCCESS, response);
    }

}
