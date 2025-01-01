package com.bobjool.reservation.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.reservation.application.dto.reservation.ReservationResDto;
import com.bobjool.reservation.application.service.ReservationService;
import com.bobjool.reservation.presentation.dto.reservation.ReservationCreateReqDto;
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
@RequestMapping("/api/v1/reservations")
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResDto>> createReservation(@Valid @RequestBody ReservationCreateReqDto reservationCreateReqDto) {
        log.info("createReservation.reservationCreateReqDto: {}", reservationCreateReqDto);
        ReservationResDto response = reservationService.createReservation(reservationCreateReqDto.toServiceDto());
        return ApiResponse.success(SuccessCode.SUCCESS_INSERT, response);
    }
}
