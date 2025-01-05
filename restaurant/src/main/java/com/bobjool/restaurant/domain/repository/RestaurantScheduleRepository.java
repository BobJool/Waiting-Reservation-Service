package com.bobjool.restaurant.domain.repository;

import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantScheduleRepository {

  Optional<RestaurantSchedule> findById(UUID id);

  RestaurantSchedule save(RestaurantSchedule restaurantSchedule);

  Page<RestaurantSchedule> findAllByIsDeletedFalse(Pageable pageable);

  Page<RestaurantSchedule> findAllByRestaurantId(UUID restaurantId, Pageable pageable);

  Page<RestaurantSchedule> findAllByRestaurantIdAndDate(UUID restaurantId, LocalDate date, Pageable pageable);
}
