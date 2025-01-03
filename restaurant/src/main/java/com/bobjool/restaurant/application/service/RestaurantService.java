package com.bobjool.restaurant.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantCreateDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantResDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantUpdateDto;
import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.repository.RestaurantRepository;
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
  public void deleteRestaurant(UUID Id){
    log.info("DeleteRestaurant");

    Restaurant restaurant = restaurantRepository.findById(Id)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    restaurant.deleteBase(restaurant.getUserId());
  }

  @Transactional(readOnly = true)
  public Page<RestaurantResDto> AllRestaurants(Pageable pageable){
    log.info("pageable second All info");

    Page<Restaurant> restaurantPage = restaurantRepository.findAllByIsDeletedFalse(pageable);

    return restaurantPage.map(RestaurantResDto::from);
  }

    private void validateDuplicate(RestaurantCreateDto restaurantCreateDto) {
    if(restaurantRepository.findByRestaurantName(restaurantCreateDto.restaurantName()).isPresent())
      throw new BobJoolException(ErrorCode.DUPLICATED_NAME);
    if(restaurantRepository.findByRestaurantPhone(restaurantCreateDto.restaurantPhone()).isPresent())
      throw new BobJoolException(ErrorCode.DUPLICATED_PHONE);
    if(restaurantRepository.findByRestaurantAddressDetail(restaurantCreateDto.restaurantAddressDetail()).isPresent())
      throw new BobJoolException(ErrorCode.DUPPLICATED_Address);
  }

//  Update 임시 예외처리로 Create와 공용사용중. 추후 세분화(바꾸기 이전 값을 따로 예외처리)
  private void validateDuplicate(RestaurantUpdateDto restaurantUpdateDto) {
    if(restaurantRepository.findByRestaurantName(restaurantUpdateDto.restaurantName()).isPresent())
      throw new BobJoolException(ErrorCode.DUPLICATED_NAME);
    if(restaurantRepository.findByRestaurantPhone(restaurantUpdateDto.restaurantPhone()).isPresent())
      throw new BobJoolException(ErrorCode.DUPLICATED_PHONE);
    if(restaurantRepository.findByRestaurantAddressDetail(restaurantUpdateDto.restaurantAddressDetail()).isPresent())
      throw new BobJoolException(ErrorCode.DUPPLICATED_Address);
  }

}
