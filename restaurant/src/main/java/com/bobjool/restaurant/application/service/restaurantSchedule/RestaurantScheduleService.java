package com.bobjool.restaurant.application.service.restaurantSchedule;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleCreateDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleResDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleReserveDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleUpdateDto;
import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import com.bobjool.restaurant.domain.repository.RestaurantRepository;
import com.bobjool.restaurant.domain.repository.RestaurantScheduleRepository;
import java.time.LocalDate;
import java.time.LocalTime;
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
  private final RestaurantRepository restaurantRepository;

  @Transactional
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
  @Transactional
  public RestaurantScheduleResDto reserveSchedule(UUID scheduleId,
      RestaurantScheduleReserveDto scheduleReserveDto) {
    log.info("updateSchedule.ScheduleReserveDto = {}", scheduleReserveDto);

    RestaurantSchedule restaurantSchedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    if (!restaurantSchedule.isCapacityExceeded(
        scheduleReserveDto.currentCapacity())) {
      log.info("restaurantSchedule.getMaxCapacity = {}", restaurantSchedule.getMaxCapacity());
      log.info("scheduleUpdateDto.currentCapacity = {}", scheduleReserveDto.currentCapacity());
      throw new BobJoolException(ErrorCode.CAPACITY_OVERFLOW);
    }

    if (!restaurantSchedule.isAvailable()) {
      throw new BobJoolException(ErrorCode.ALREADEY_RESERVED);
    }
    restaurantSchedule.reserve(
        scheduleReserveDto.userId(),
        scheduleReserveDto.currentCapacity()
    );
    return RestaurantScheduleResDto.from(restaurantSchedule);
  }

  //for owner
  @Transactional
  public RestaurantScheduleResDto updateSchedule(UUID scheduleId,
      RestaurantScheduleUpdateDto scheduleUpdateDto) {
    log.info("updateSchedule.ScheduleUpdateDto = {}", scheduleUpdateDto);

    RestaurantSchedule restaurantSchedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    restaurantSchedule.update(
        scheduleUpdateDto.userId(),
        scheduleUpdateDto.date(),
        scheduleUpdateDto.timeSlot(),
        scheduleUpdateDto.maxCapacity(),
        scheduleUpdateDto.currentCapacity(),
        scheduleUpdateDto.available()
    );

    return RestaurantScheduleResDto.from(restaurantSchedule);
  }

  @Transactional
  public void deleteSchedule(UUID Id) {
    log.info("DeleteSchedule");

    RestaurantSchedule schedule = scheduleRepository.findById(Id)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    schedule.deleteBase(schedule.getUserId());
  }

  @Transactional(readOnly = true)
  public Page<RestaurantScheduleResDto> AllSchedules(Pageable pageable) {
    log.info("All RestaurantSchedule info");

    Page<RestaurantSchedule> SchedulePage = scheduleRepository.findAllByIsDeletedFalse(pageable);

    return SchedulePage.map(RestaurantScheduleResDto::from);
  }

  @Transactional(readOnly = true)
  public Page<RestaurantScheduleResDto> readForOneRestaurant(UUID id, Pageable pageable) {
    log.info("All RestaurantSchedule info");

    Page<RestaurantSchedule> SchedulePage = scheduleRepository.findAllByRestaurantId(id, pageable);

    return SchedulePage.map(RestaurantScheduleResDto::from);
  }


  public Page<RestaurantScheduleResDto> findAllByRestaurantIdAndDate(UUID restaurantId,
      LocalDate date, Pageable pageable) {
    log.info("All RestaurantSchedule info");

    Page<RestaurantSchedule> SchedulePage = scheduleRepository.findAllByRestaurantIdAndDate(
        restaurantId, date, pageable);

    return SchedulePage.map(RestaurantScheduleResDto::from);
  }

  @Transactional
  public Page<RestaurantScheduleResDto> createDailySchedule(int term, LocalDate date,
      RestaurantScheduleCreateDto createDto) {

    Restaurant restaurant = restaurantRepository.findById(createDto.restaurantId())
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    int reserveTime = createDto.timeSlot().getHour();
    log.info("reserveTime={}", reserveTime);
    log.info("restaurant.getCloseTime().getHour()={}", restaurant.getCloseTime().getHour());

    while(reserveTime<restaurant.getCloseTime().getHour()){
      log.info("reserveTime={}", reserveTime);
      log.info("restaurant.getCloseTime().getHour()={}", restaurant.getCloseTime().getHour());
      RestaurantSchedule schedule = RestaurantSchedule.create(
          createDto.userId(),
          createDto.restaurantId(),
          createDto.tableNumber(),
          createDto.date(),
          LocalTime.of(reserveTime,0),
          createDto.maxCapacity(),
          0,
          true);

      RestaurantScheduleResDto.from(scheduleRepository.save(schedule)
      );
      reserveTime = reserveTime+term;
    }

    Pageable pageable = Pageable.unpaged();
    Page<RestaurantSchedule> SchedulePage = scheduleRepository.findAllByRestaurantIdAndDate(
        restaurant.getId(), date, pageable);

    return SchedulePage.map(RestaurantScheduleResDto::from);
  }
}
