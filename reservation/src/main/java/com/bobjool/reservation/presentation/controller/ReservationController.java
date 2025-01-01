package com.bobjool.reservation.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.reservation.application.dto.reservation.ReservationResDto;
import com.bobjool.reservation.application.service.ReservationService;
import com.bobjool.reservation.presentation.dto.reservation.ReservationCreateReqDto;
import com.bobjool.reservation.presentation.dto.reservation.ReservationUpdateReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResDto>> createReservation(@Valid @RequestBody ReservationCreateReqDto reqDto) {
        log.info("createReservation.reservationCreateReqDto: {}", reqDto);
        ReservationResDto response = reservationService.createReservation(reqDto.toServiceDto());
        return ApiResponse.success(SuccessCode.SUCCESS_INSERT, response);
    }

    @PatchMapping("/status/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResDto>> updateReservationStatus(@Valid @RequestBody ReservationUpdateReqDto reqDto,
                                                                                  @PathVariable("reservationId") UUID reservationId) {
        log.info("updateReservationStatus.reservationUpdateReqDto: {}", reqDto);
        ReservationResDto response = reservationService.updateReservationStatus(reqDto.toServiceDto(), reservationId);
        return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
    }

    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResDto>> cancelReservation(@PathVariable("reservationId") UUID reservationId) {
        log.info("cancelReservation.reservationCancelReqDto: {}", reservationId);
        ReservationResDto response = reservationService.cancelReservation(reservationId);
        return ApiResponse.success(SuccessCode.SUCCESS, response);
    }
}
