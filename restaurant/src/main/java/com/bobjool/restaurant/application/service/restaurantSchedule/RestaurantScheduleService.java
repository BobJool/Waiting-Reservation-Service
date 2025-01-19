package com.bobjool.restaurant.application.service.restaurantSchedule;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleCreateDto;
import com.bobjool.restaurant.application.dto.restaurantSchedule.RestaurantScheduleForCustomerResDto;
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

import com.bobjool.restaurant.infrastructure.aspect.DistributedLock;
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

    restaurantRepository.findById(createDto.restaurantId())
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    if(createDto.date().isBefore(LocalDate.now())){
        throw new BobJoolException(ErrorCode.INVALID_DATE);
    }

    boolean isDuplicate = scheduleRepository.existsByRestaurantIdAndTableNumberAndDateAndTimeSlot(createDto.restaurantId(),
    createDto.tableNumber(),
    createDto.date(),
    createDto.timeSlot()
    );

    if (isDuplicate) {
    throw new BobJoolException(ErrorCode.DUPLICATE_SCHEDULE);
        }


        RestaurantSchedule schedule = RestaurantSchedule.create(createDto.restaurantId(),
            createDto.tableNumber(),
            createDto.date(),
            createDto.timeSlot(),
            createDto.maxCapacity(),
            0,
            true
        );

        return RestaurantScheduleResDto.from(scheduleRepository.save(schedule));
    }

    @Transactional
    @DistributedLock(key = "'reservation:schedule:lock:' + #scheduleId")
    public RestaurantScheduleResDto reserveSchedule(UUID scheduleId,
    RestaurantScheduleReserveDto scheduleReserveDto) {

        //레스토랑 reserve 로직
        RestaurantSchedule tempRestaurantSchedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

    if (!tempRestaurantSchedule.isCapacityExceeded(
        scheduleReserveDto.currentCapacity())) {
        log.info("restaurantSchedule.getMaxCapacity = {}", tempRestaurantSchedule.getMaxCapacity());
        log.info("scheduleUpdateDto.currentCapacity = {}", scheduleReserveDto.currentCapacity());
            throw new BobJoolException(ErrorCode.CAPACITY_OVERFLOW);
        }

        if (!tempRestaurantSchedule.isAvailable()) {
            throw new BobJoolException(ErrorCode.ALREADEY_RESERVED);
        }
        tempRestaurantSchedule.reserve(
                scheduleReserveDto.userId(),
                scheduleReserveDto.currentCapacity()
        );
        return RestaurantScheduleResDto.from(tempRestaurantSchedule);
    }

    //for owner
    @Transactional
    public RestaurantScheduleResDto updateSchedule(UUID scheduleId,
                                                   RestaurantScheduleUpdateDto updateDto) {
        log.info("updateSchedule.ScheduleUpdateDto = {}", updateDto);

        RestaurantSchedule restaurantSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        if (updateDto.date().isBefore(LocalDate.now())) {
            throw new BobJoolException(ErrorCode.INVALID_DATE);
        }

        restaurantSchedule.update(
                updateDto.date(),
                updateDto.timeSlot(),
                updateDto.maxCapacity(),
                updateDto.currentCapacity(),
                updateDto.available()
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

        Page<RestaurantSchedule> SchedulePage = scheduleRepository.findAllByIsDeletedFalse(pageable);

        return SchedulePage.map(RestaurantScheduleResDto::from);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantScheduleResDto> readForOneRestaurant(UUID id, Pageable pageable) {

        Page<RestaurantSchedule> SchedulePage = scheduleRepository.findAllByRestaurantId(id, pageable);

        return SchedulePage.map(RestaurantScheduleResDto::from);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantScheduleResDto> findAllByRestaurantIdAndDate(UUID restaurantId,
                                                                       LocalDate date, Pageable pageable) {

        Page<RestaurantSchedule> SchedulePage = scheduleRepository.findAllByRestaurantIdAndDate(
                restaurantId, date, pageable);

        return SchedulePage.map(RestaurantScheduleResDto::from);
    }
    @Transactional
    public Page<RestaurantScheduleResDto> createDailySchedule(int term, LocalDate date,
       RestaurantScheduleCreateDto createDto) {

        Restaurant restaurant = restaurantRepository.findById(createDto.restaurantId())
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        if (date.isBefore(LocalDate.now())) {
            throw new BobJoolException(ErrorCode.INVALID_DATE);
        }

        int reserveTime = createDto.reserveTime();
        log.info("reserveTime={}", reserveTime);
        log.info("restaurant.getCloseTime().getHour()={}", restaurant.getCloseTime().getHour());

        while (reserveTime < restaurant.getCloseTime().getHour()) {
            log.info("reserveTime={}", reserveTime);
            log.info("restaurant.getCloseTime().getHour()={}", restaurant.getCloseTime().getHour());
            RestaurantSchedule schedule = RestaurantSchedule.create(
                    createDto.restaurantId(),
                    createDto.tableNumber(),
                    createDto.date(),
                    LocalTime.of(reserveTime, 0),
                    createDto.maxCapacity(),
                    0,
                    true);

            RestaurantScheduleResDto.from(scheduleRepository.save(schedule)
            );
            reserveTime = reserveTime + term;
        }

        Pageable pageable = Pageable.unpaged();
        Page<RestaurantSchedule> SchedulePage = scheduleRepository.findAllByRestaurantIdAndDate(
                restaurant.getId(), date, pageable);

        return SchedulePage.map(RestaurantScheduleResDto::from);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantScheduleForCustomerResDto> readForUserReserve(Long userId, Pageable pageable) {
        log.info("RestaurantSchedule info");

        Page<RestaurantSchedule> SchedulePage = scheduleRepository.findAllByUserId(userId, pageable);

        return SchedulePage.map(RestaurantScheduleForCustomerResDto::from);
    }
}
