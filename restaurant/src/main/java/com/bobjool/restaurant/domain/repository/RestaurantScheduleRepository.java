package com.bobjool.restaurant.domain.repository;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantScheduleRepository {

  Optional<RestaurantSchedule> findById(UUID id);

  RestaurantSchedule save(RestaurantSchedule restaurantSchedule);

}
