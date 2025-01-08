package com.bobjool.restaurant.presentation.controller.restaurant;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.PageResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantContactResDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantForCustomerResDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantForMasterResDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantResDto;
import com.bobjool.restaurant.application.service.restaurant.RestaurantService;
import com.bobjool.restaurant.presentation.dto.restaurant.RestaurantCreateReqDto;
import com.bobjool.restaurant.presentation.dto.restaurant.RestaurantUpdateReqDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@Slf4j
@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

  private final RestaurantService restaurantService;

  @PostMapping
  public ResponseEntity<ApiResponse<RestaurantResDto>> createRestaurant(
      @Valid @RequestBody RestaurantCreateReqDto restaurantCreateReqDto) {
    log.info("create.RestaurantCreateDto={}", restaurantCreateReqDto);
    RestaurantResDto response = restaurantService.createRestaurant(
        restaurantCreateReqDto.toServiceDto());
    return ApiResponse.success(SuccessCode.SUCCESS_INSERT, response);
  }

  @PutMapping("/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantResDto>> updateRestaurant(
      @Valid @RequestBody RestaurantUpdateReqDto restaurantUpdateReqDto,
      @PathVariable("restaurantId") UUID restaurantId) {
    log.info("update.RestaurantUpdateReqDto={}", restaurantUpdateReqDto);
    RestaurantResDto response = restaurantService.updateRestaurant(restaurantId,
        restaurantUpdateReqDto.toServiceDto());
    return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
  }

  @PutMapping("/is-reservation/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantResDto>> restaurantReservationChange(
      @Valid @RequestParam boolean isReservation,
      @PathVariable("restaurantId") UUID restaurantId) {
    log.info("isReservation={}", isReservation);
    RestaurantResDto response = restaurantService.isReservation(restaurantId, isReservation);
    return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
  }

  @PutMapping("/is-queue/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantResDto>> restaurantQueueChange(
      @Valid @RequestParam boolean isQueue,
      @PathVariable("restaurantId") UUID restaurantId) {
    log.info("isQueue={}", isQueue);
    RestaurantResDto response = restaurantService.isQueue(restaurantId, isQueue);
    return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
  }

  @DeleteMapping("/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantResDto>> updateRestaurant(
      @Valid @PathVariable("restaurantId") UUID restaurantId) {

    log.info("RestaurantDelete");
    restaurantService.deleteRestaurant(restaurantId);
    return ApiResponse.success(SuccessCode.SUCCESS_DELETE);
  }

  //모든 음식점 정보 전체 조회
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<RestaurantResDto>>> getAllRestaurants(
      @SortDefault(sort = "createdAt", direction = Direction.DESC)
      Pageable pageable) {
    log.info("getAllRestaurants");

    Page<RestaurantResDto> resPage
        = restaurantService.readRestaurants(pageable);
    return ApiResponse.success(SuccessCode.SUCCESS, PageResponse.of(resPage));
  }

  //삭제된 음식점 정보 전체 조회
  @GetMapping("/deleted")
  public ResponseEntity<ApiResponse<PageResponse<RestaurantResDto>>> getDeletedRestaurants(
      @SortDefault(sort = "createdAt", direction = Direction.DESC)
      Pageable pageable) {
    log.info("getDeletedRestaurants");

    Page<RestaurantResDto> resPage
        = restaurantService.deletedRestaurants(pageable);
    return ApiResponse.success(SuccessCode.SUCCESS, PageResponse.of(resPage));
  }

  //음식점 정보 검색

  //단일 음식점 정보 조회(for Owner)
  @GetMapping("/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantResDto>> getRestaurantsForOwner(
      @Valid @PathVariable("restaurantId") UUID restaurantId) {
    log.info("getAllRestaurants");

    RestaurantResDto response = restaurantService.readRestaurantsForOwner(restaurantId);
    return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
  }

  //단일 음식점 정보 조회(for Owner)
  @GetMapping("/customer/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantForCustomerResDto>> getRestaurantsForCustomer(
      @Valid @PathVariable("restaurantId") UUID restaurantId) {
    log.info("getAllRestaurants");

    RestaurantForCustomerResDto response = restaurantService.readRestaurantsForCustomer(restaurantId);
    return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
  }
  //단일 음식점 정보 조회(for Master)
  @GetMapping("/master/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantForMasterResDto>> getRestaurantsForMaster(
      @Valid @PathVariable("restaurantId") UUID restaurantId) {
    log.info("getAllRestaurants");

    RestaurantForMasterResDto response = restaurantService.readRestaurantsForMaster(restaurantId);
    return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
  }

  //단일 음식점 정보 조회(for Owner)
  @GetMapping("/{restaurantId}/contact")
  public ResponseEntity<ApiResponse<RestaurantContactResDto>> getRestaurantContact(
      @Valid @PathVariable("restaurantId") UUID restaurantId) {
    log.info("getRestaurantContact");

    RestaurantContactResDto response = restaurantService.ReadRestaurantContact(restaurantId);
    return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED, response);
  }

  //상세 검색
  @GetMapping("/detail")
  public ResponseEntity<ApiResponse<PageResponse<RestaurantForCustomerResDto>>> searchByDetail(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String region,
      @RequestParam(required = false) String addressDetail,
      @RequestParam(required = false) String description,
      @SortDefault(sort = "createdAt", direction = Direction.DESC)
      Pageable pageable)
  {
    Page<RestaurantForCustomerResDto> resPage = restaurantService.searchByDetail( name, region, addressDetail, description, pageable);
    return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED, PageResponse.of(resPage));
  }

  //전체 검색
  @GetMapping("/keyword")
  public ResponseEntity<ApiResponse<PageResponse<RestaurantForCustomerResDto>>> searchByDetail(
      @RequestParam(required = false) String keyword,
      @SortDefault(sort = "createdAt", direction = Direction.DESC)
      Pageable pageable)
  {
    Page<RestaurantForCustomerResDto> resPage = restaurantService.searchByKeyWord(keyword, pageable);
    return ApiResponse.success(SuccessCode.SUCCESS_ACCEPTED, PageResponse.of(resPage));
  }


}
