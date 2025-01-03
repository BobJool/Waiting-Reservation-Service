package com.bobjool.restaurant.application.service.restaurantSchedule;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantUpdateDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleCreateDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleResDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleReserveDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleUpdateDto;
import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import com.bobjool.restaurant.domain.repository.RestaurantScheduleRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        createDto.maxCapacity(),
        0,
        true
    );

    return RestaurantScheduleResDto.from(scheduleRepository.save(schedule));
  }

  //for customer
  public RestaurantScheduleResDto reserveSchedule(UUID scheduleId, RestaurantScheduleReserveDto scheduleReserveDto) {
    log.info("updateSchedule.ScheduleReserveDto = {}", scheduleReserveDto);

    RestaurantSchedule restaurantSchedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    if(restaurantSchedule.getMaxCapacity() < scheduleReserveDto.currentCapacity()){
      log.info("restaurantSchedule.getMaxCapacity = {}", restaurantSchedule.getMaxCapacity());
      log.info("scheduleUpdateDto.currentCapacity = {}", scheduleReserveDto.currentCapacity());
      throw new BobJoolException(ErrorCode.CAPACITY_OVERFLOW);
    }
    if(!restaurantSchedule.isAvailable()){
      throw new BobJoolException(ErrorCode.ALREADEY_RESERVED);
    }
    restaurantSchedule.reserve(scheduleReserveDto);
    return RestaurantScheduleResDto.from(restaurantSchedule);
  }

  //for owner
  public RestaurantScheduleResDto updateSchedule(UUID scheduleId, RestaurantScheduleUpdateDto scheduleUpdateDto) {
    log.info("updateSchedule.ScheduleUpdateDto = {}", scheduleUpdateDto);

    RestaurantSchedule restaurantSchedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    restaurantSchedule.update(scheduleUpdateDto);

    return RestaurantScheduleResDto.from(restaurantSchedule);
  }

//  public void deleteSchedule(@Valid UUID restaurantId) {
//  }

//  public Page<RestaurantResDto> AllSchedules(Pageable allRestaurantPageable) {
//  }


}
