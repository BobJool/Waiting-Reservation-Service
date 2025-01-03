package com.bobjool.restaurant.presentation.controller.restaurant;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.PageResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantResDto;
import com.bobjool.restaurant.application.service.RestaurantService;
import com.bobjool.restaurant.presentation.dto.restaurant.RestaurantCreateReqDto;
import com.bobjool.restaurant.presentation.dto.restaurant.RestaurantUpdateReqDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

  @DeleteMapping("/{restaurantId}")
  public ResponseEntity<ApiResponse<RestaurantResDto>> updateRestaurant(
      @Valid @PathVariable("restaurantId") UUID restaurantId) {

    log.info("RestaurantDelete");
    restaurantService.deleteRestaurant(restaurantId);
    return ApiResponse.success(SuccessCode.SUCCESS_DELETE);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<RestaurantResDto>>> getAllRestaurants(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @SortDefault(sort = "createdAt", direction = Direction.DESC)
      Pageable pageable) {
    log.info("getAllRestaurants");

    Pageable AllRestaurantPageable = PageRequest.of(page, size, pageable.getSort());

    Page<RestaurantResDto> resPage
        = restaurantService.AllRestaurants(AllRestaurantPageable);
    return ApiResponse.success(SuccessCode.SUCCESS, PageResponse.of(resPage));

  }

}
