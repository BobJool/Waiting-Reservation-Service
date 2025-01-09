package com.bobjool.restaurant.domain.repository;

import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantScheduleRepository {

  Optional<RestaurantSchedule> findById(UUID id);

  Optional<RestaurantSchedule> findByDate(LocalDate date);

  Optional<RestaurantSchedule> findByTableNumber(int tableNumber);


  RestaurantSchedule save(RestaurantSchedule restaurantSchedule);

  Page<RestaurantSchedule> findAllByIsDeletedFalse(Pageable pageable);

  Page<RestaurantSchedule> findAllByRestaurantId(UUID restaurantId, Pageable pageable);

  Page<RestaurantSchedule> findAllByRestaurantIdAndDate(UUID restaurantId, LocalDate date, Pageable pageable);

  Page<RestaurantSchedule> findAllByUserId(Long userId, Pageable pageable);

  boolean existsByRestaurantIdAndTableNumberAndDateAndTimeSlot(
      UUID restaurantId,
      int tableNumber,
      LocalDate date,
      LocalTime timeSlot
  );

}
