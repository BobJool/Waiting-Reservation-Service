package com.bobjool.restaurant.application.service.restaurantSchedule;

import com.bobjool.restaurant.application.dto.restaurant.RestaurantCreateDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantResDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantUpdateDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleCreateDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleResDto;
import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import com.bobjool.restaurant.domain.repository.RestaurantRepository;
import com.bobjool.restaurant.domain.repository.RestaurantScheduleRepository;
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
public class RestaurantScheduleService {

  private final RestaurantScheduleRepository scheduleRepository;

  public RestaurantScheduleResDto createSchedule(RestaurantScheduleCreateDto createDto) {

    RestaurantSchedule schedule = RestaurantSchedule.create(
        createDto.userId(),
        createDto.restaurantId(),
        createDto.tableNumber(),
        createDto.date(),
        createDto.timeSlot(),
        createDto.maxTableCapacity(),
        0,
        true
    );

    return RestaurantScheduleResDto.from(scheduleRepository.save(schedule));
  }

//  public RestaurantResDto updateSchedule(UUID restaurantId, RestaurantUpdateDto serviceDto) {
//  }

//  public void deleteSchedule(@Valid UUID restaurantId) {
//  }

//  public Page<RestaurantResDto> AllSchedules(Pageable allRestaurantPageable) {
//  }
}
