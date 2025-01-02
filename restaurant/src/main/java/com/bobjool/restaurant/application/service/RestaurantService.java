package com.bobjool.restaurant.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.restaurant.application.dto.RestaurantCreateDto;
import com.bobjool.restaurant.application.dto.RestaurantResDto;
import com.bobjool.restaurant.domain.entity.Restaurant;
import com.bobjool.restaurant.domain.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


  private void validateDuplicate(RestaurantCreateDto restaurantCreateDto) {
    if(restaurantRepository.findByRestaurantName(restaurantCreateDto.restaurantName()).isPresent())
      throw new BobJoolException(ErrorCode.DUPLICATED_NAME);
    if(restaurantRepository.findByRestaurantPhone(restaurantCreateDto.restaurantPhone()).isPresent())
      throw new BobJoolException(ErrorCode.DUPLICATED_PHONE);
    if(restaurantRepository.findByRestaurantAddressDetail(restaurantCreateDto.restaurantAddressDetail()).isPresent())
      throw new BobJoolException(ErrorCode.DUPPLICATED_Address);
  }

}
