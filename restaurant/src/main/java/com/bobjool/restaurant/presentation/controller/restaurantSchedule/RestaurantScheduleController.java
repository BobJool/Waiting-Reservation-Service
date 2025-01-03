package com.bobjool.restaurant.presentation.controller.restaurantSchedule;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleResDto;
import com.bobjool.restaurant.application.service.restaurantSchedule.RestaurantScheduleService;
import com.bobjool.restaurant.presentation.dto.restaurantSchedule.RestaurantScheduleCreateReqDto;
import com.bobjool.restaurant.presentation.dto.restaurantSchedule.RestaurantScheduleReserveReqDto;
import com.bobjool.restaurant.presentation.dto.restaurantSchedule.RestaurantScheduleUpdateReqDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/restaurants/schedule")
@RequiredArgsConstructor
public class RestaurantScheduleController {

  private final RestaurantScheduleService scheduleService;

  //음식점 스케쥴 생성 예정
  @PostMapping("/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantScheduleResDto>> createSchedule(
      @Valid @RequestBody RestaurantScheduleCreateReqDto scheduleCreateReqDto) {
    log.info("create.RestaurantScheduleCreateReqDto={}", scheduleCreateReqDto);
    RestaurantScheduleResDto response = scheduleService.createSchedule(
        scheduleCreateReqDto.toServiceDto());
    return ApiResponse.success(SuccessCode.SUCCESS_INSERT, response);
  }

  //생성된 음식점 스케쥴을 Customer가 예약
  @PatchMapping("/{ScheduleId}")
  public ResponseEntity<ApiResponse<RestaurantScheduleResDto>> reserveSchedule(
      @Valid @RequestBody RestaurantScheduleReserveReqDto scheduleReserveReqDto,
      @PathVariable("ScheduleId") UUID ScheduleId) {
    log.info("update.RestaurantUpdateReqDto={}", scheduleReserveReqDto);
    RestaurantScheduleResDto response = scheduleService.reserveSchedule(ScheduleId,
        scheduleReserveReqDto.toServiceDto());
    return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
  }

  //음식점 스케쥴 Owner가 수정
  @PatchMapping("/owner/{ScheduleId}")
  public ResponseEntity<ApiResponse<RestaurantScheduleResDto>> updateSchedule(
      @Valid @RequestBody RestaurantScheduleUpdateReqDto scheduleUpdateReqDto,
      @PathVariable("ScheduleId") UUID ScheduleId) {
    log.info("update.RestaurantUpdateReqDto={}", scheduleUpdateReqDto);
    RestaurantScheduleResDto response = scheduleService.updateSchedule(ScheduleId,
        scheduleUpdateReqDto.toServiceDto());
    return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
  }

  //음식점 스케쥴 삭제
  @DeleteMapping("/owner/{ScheduleId}")
  public ResponseEntity<ApiResponse<RestaurantScheduleResDto>> deleteSchedule(
      @Valid @PathVariable("ScheduleId") UUID ScheduleId) {

    log.info("RestaurantDelete");
    scheduleService.deleteSchedule(ScheduleId);
    return ApiResponse.success(SuccessCode.SUCCESS_DELETE);
  }
//
//  //모든 생성된 음식점 스케쥴 전체 조회
//  @GetMapping
//  public ResponseEntity<ApiResponse<PageResponse<RestaurantResDto>>> getAllSchedule(
//      @RequestParam(defaultValue = "0") int page,
//      @RequestParam(defaultValue = "10") int size,
//      @SortDefault(sort = "createdAt", direction = Direction.DESC)
//      Pageable pageable) {
//    log.info("getAllRestaurants");
//
//    Pageable AllRestaurantPageable = PageRequest.of(page, size, pageable.getSort());
//
//    Page<RestaurantResDto> resPage
//        = scheduleService.AllSchedules(AllRestaurantPageable);
//    return ApiResponse.success(SuccessCode.SUCCESS, PageResponse.of(resPage));
//  }

  //해성님(예약) 필요 기능 | 예약시 스케쥴의 인원수를 차감하는 기능(재고 차감st API)

  //해성님(예약) 필요 기능 | 예약 실패시 스케쥴의 인원수를 롤백하는 기능
}
