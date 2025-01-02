package com.bobjool.restaurant.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.restaurant.application.dto.RestaurantResDto;
import com.bobjool.restaurant.application.service.RestaurantService;
import com.bobjool.restaurant.presentation.dto.RestaurantCreateReqDto;
import com.bobjool.restaurant.presentation.dto.RestaurantUpdateReqDto;
import feign.Response;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

  private final RestaurantService restaurantService;

  @PostMapping
  public ResponseEntity<ApiResponse<RestaurantResDto>> createRestaurant(@Valid @RequestBody RestaurantCreateReqDto restaurantCreateReqDto) {
    log.info("create.RestaurantCreateDto={}", restaurantCreateReqDto);
    RestaurantResDto response = restaurantService.createRestaurant(restaurantCreateReqDto.toServiceDto());
    return ApiResponse.success(SuccessCode.SUCCESS_INSERT, response);
  }

  @PutMapping("/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantResDto>> updateRestaurant(@Valid @RequestBody RestaurantUpdateReqDto restaurantUpdateReqDto,
                                                                        @PathVariable("restaurantId") UUID restaurantId ) {
    log.info("update.RestaurantUpdateReqDto={}", restaurantUpdateReqDto);
    RestaurantResDto response = restaurantService.updateRestaurant(restaurantId , restaurantUpdateReqDto.toServiceDto());
    return ApiResponse.success(SuccessCode.SUCCESS_UPDATE, response);
  }



}
