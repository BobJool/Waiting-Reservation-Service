package com.bobjool.reservation.presentation.controller;

import com.bobjool.common.infra.aspect.RequireRole;
import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.PageResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.reservation.application.dto.reservation.ReservationResDto;
import com.bobjool.reservation.application.dto.reservation.ReservationSearchDto;
import com.bobjool.reservation.application.service.ReservationService;
import com.bobjool.reservation.presentation.dto.reservation.ReservationCreateReqDto;
import com.bobjool.reservation.presentation.dto.reservation.ReservationUpdateReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    @RequireRole(value = {"CUSTOMER"})
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResDto>> createReservation(@Valid @RequestBody ReservationCreateReqDto reqDto,
                                                                            @RequestHeader(value = "X-User-Id", required = true) String userId,
                                                                            @RequestHeader(value = "X-Role", required = true) String role) {
        ReservationResDto response = reservationService.createReservation(reqDto.toServiceDto(), Long.valueOf(userId), role);
        return ApiResponse.success(SuccessCode.SUCCESS_INSERT, response);
    }

    @RequireRole(value = {"MASTER"})
    @PatchMapping("/status/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResDto>> updateReservationStatus(@Valid @RequestBody ReservationUpdateReqDto reqDto,
                                                                                  @PathVariable("reservationId") UUID reservationId) {
        ReservationResDto response = reservationService.updateReservationStatus(reqDto.toServiceDto(), reservationId);
        return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
    }

    @RequireRole(value = {"OWNER", "CUSTOMER"})
    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResDto>> cancelReservation(@PathVariable("reservationId") UUID reservationId,
                                                                            @RequestHeader(value = "X-User-Id", required = true) String userId,
                                                                            @RequestHeader(value = "X-Role", required = true) String role) {
        ReservationResDto response = reservationService.cancelReservation(reservationId, role);
        return ApiResponse.success(SuccessCode.SUCCESS, response);
    }

    @RequireRole(value = {"CUSTOMER", "OWNER", "MASTER"})
    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResDto>> getReservation(@PathVariable("reservationId") UUID reservationId,
                                                                         @RequestHeader(value = "X-User-Id", required = true) String userId,
                                                                         @RequestHeader(value = "X-Role", required = true) String role) {
        ReservationResDto response = reservationService.getReservation(reservationId, Long.valueOf(userId), role);
        return ApiResponse.success(SuccessCode.SUCCESS, response);
    }

    @RequireRole(value = {"MASTER"})
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReservationResDto>>> search(@RequestParam(value = "userId", required = false) Long userId,
                                                                          @RequestParam(value = "restaurantId", required = false) UUID restaurantId,
                                                                          @RequestParam(value = "restaurantScheduleId", required = false) UUID restaurantScheduleId,
                                                                          @RequestParam(value = "status", required = false) String status,
                                                                          @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                                                                           Pageable pageable) {

        Page<ReservationResDto> result = reservationService.search(
                new ReservationSearchDto(userId, restaurantId, restaurantScheduleId, status)
                , pageable);
        PageResponse<ReservationResDto> response = PageResponse.of(result);
        return ApiResponse.success(SuccessCode.SUCCESS, response);
    }

}
