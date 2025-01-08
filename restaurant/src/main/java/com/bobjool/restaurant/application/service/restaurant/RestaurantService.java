package com.bobjool.restaurant.application.service.restaurant;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantContactResDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantCreateDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantForCustomerResDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantForMasterResDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantResDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantUpdateDto;
import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.repository.RestaurantRepository;
import com.bobjool.restaurant.infrastructure.repository.restaurant.RestaurantRepositoryCustom;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantService {

  private final RestaurantRepository restaurantRepository;
  private final RestaurantRepositoryCustom restaurantRepositoryImpl;

  @Transactional
  public RestaurantResDto createRestaurant(RestaurantCreateDto restaurantCreateDto) {
    log.info("createRestaurant.restaurantCreateDto = {}", restaurantCreateDto);

    validateDuplicate(restaurantCreateDto);

    Restaurant restaurant = Restaurant.create(
        restaurantCreateDto.userId(),
        restaurantCreateDto.restaurantCategory(),
        restaurantCreateDto.restaurantPhone(),
        restaurantCreateDto.restaurantName(),
        restaurantCreateDto.restaurantRegion(),
        restaurantCreateDto.restaurantAddressDetail(),
        restaurantCreateDto.restaurantDescription(),
        restaurantCreateDto.restaurantVolume(),
        restaurantCreateDto.isReservation(),
        restaurantCreateDto.isQueue(),
        restaurantCreateDto.openTime(),
        restaurantCreateDto.closeTime()
    );

    return RestaurantResDto.from(restaurantRepository.save(restaurant));

  }

  @Transactional
  public RestaurantResDto updateRestaurant(UUID Id, RestaurantUpdateDto restaurantUpdateDto) {
    log.info("updateRestaurant.restaurantUpdateDto = {}", restaurantUpdateDto);

    validateDuplicate(restaurantUpdateDto);
    Restaurant restaurant = restaurantRepository.findById(Id)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    restaurant.update(restaurantUpdateDto);

    return RestaurantResDto.from(restaurant);
  }

  @Transactional
  public void deleteRestaurant(UUID Id) {
    log.info("DeleteRestaurant");

    Restaurant restaurant = restaurantRepository.findById(Id)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    restaurant.deleteBase(restaurant.getUserId());
  }

  //음식점 모든 정보 전체 조회
  @Transactional(readOnly = true)
  public Page<RestaurantResDto> readRestaurants(Pageable pageable) {
    log.info("All Restaurant info");

    Page<Restaurant> restaurantPage = restaurantRepository.findAllByIsDeletedFalse(pageable);

    return restaurantPage.map(RestaurantResDto::from);
  }

  //삭제된 음식점 정보 전체 조회
  @Transactional(readOnly = true)
  public Page<RestaurantResDto> deletedRestaurants(Pageable pageable) {
    log.info("Deleted Restaurant info");

    Page<Restaurant> restaurantPage = restaurantRepository.findAllByIsDeletedTrue(pageable);

    return restaurantPage.map(RestaurantResDto::from);
  }


  //단일 음식점 정보 조회 For Customer
  @Transactional(readOnly = true)
  public RestaurantForCustomerResDto readRestaurantsForCustomer(UUID id) {
    log.info("All Restaurant info");

    Restaurant restaurant = restaurantRepository.findById(id)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    return RestaurantForCustomerResDto.from(restaurant);
  }

  //단일 음식점 정보 조회 For Owner
  @Transactional(readOnly = true)
  public RestaurantResDto readRestaurantsForOwner(UUID id) {
    log.info("All Restaurant info");

    Restaurant restaurant = restaurantRepository.findById(id)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    return RestaurantResDto.from(restaurant);
  }

  @Transactional(readOnly = true)
  public RestaurantForMasterResDto readRestaurantsForMaster(UUID id) {
    log.info("All Restaurant info");

    Restaurant restaurant = restaurantRepository.findById(id)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    return RestaurantForMasterResDto.from(restaurant);
  }

  @Transactional
  public RestaurantResDto isReservation(UUID restaurantId, boolean isReservation) {
    log.info("isReservation={}", isReservation);

    Restaurant restaurant = restaurantRepository.findById(restaurantId)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    restaurant.isReservation(isReservation);

    return RestaurantResDto.from(restaurant);
  }
  @Transactional
  public RestaurantResDto isQueue(UUID restaurantId, boolean isQueue) {
    log.info("isQueue={}", isQueue);

    Restaurant restaurant = restaurantRepository.findById(restaurantId)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    restaurant.isQueue(isQueue);

    return RestaurantResDto.from(restaurant);
  }

  @Transactional(readOnly = true)
  public RestaurantContactResDto ReadRestaurantContact(@Valid UUID restaurantId) {
    log.info("ReadRestaurantContact");

    Restaurant restaurant = restaurantRepository.findById(restaurantId)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    return RestaurantContactResDto.from(restaurant);
  }

  //상세 검색
  @Transactional(readOnly = true)
  public Page<RestaurantResDto> searchByDetail(
      String name, String region, String AddressDetail,
      String Description, Pageable pageable){
    Page<Restaurant> restaurantPageForCustomer = restaurantRepositoryImpl.findRestaurantPageByDeletedAtIsNull(
        name, region, AddressDetail, Description, pageable
    );
    return restaurantPageForCustomer.map(RestaurantResDto::from);
  }

  private void validateDuplicate(RestaurantCreateDto restaurantCreateDto) {
    if (restaurantRepository.findByRestaurantName(restaurantCreateDto.restaurantName())
        .isPresent()) {
      throw new BobJoolException(ErrorCode.DUPLICATED_NAME);
    }
    if (restaurantRepository.findByRestaurantPhone(restaurantCreateDto.restaurantPhone())
        .isPresent()) {
      throw new BobJoolException(ErrorCode.DUPLICATED_PHONE);
    }
    if (restaurantRepository.findByRestaurantAddressDetail(
        restaurantCreateDto.restaurantAddressDetail()).isPresent()) {
      throw new BobJoolException(ErrorCode.DUPLICATED_ADDRESS);
    }
  }

  //  Update 임시 예외처리로 Create와 공용사용중. 추후 세분화(바꾸기 이전 값을 따로 예외처리)
  private void validateDuplicate(RestaurantUpdateDto restaurantUpdateDto) {
    if (restaurantRepository.findByRestaurantName(restaurantUpdateDto.restaurantName())
        .isPresent()) {
      throw new BobJoolException(ErrorCode.DUPLICATED_NAME);
    }
    if (restaurantRepository.findByRestaurantPhone(restaurantUpdateDto.restaurantPhone())
        .isPresent()) {
      throw new BobJoolException(ErrorCode.DUPLICATED_PHONE);
    }
    if (restaurantRepository.findByRestaurantAddressDetail(
        restaurantUpdateDto.restaurantAddressDetail()).isPresent()) {
      throw new BobJoolException(ErrorCode.DUPLICATED_ADDRESS);
    }
  }



}
