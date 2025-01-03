package com.bobjool.restaurant.infrastructure.repository;

import com.bobjool.restaurant.domain.entity.restaurantSchedule.RestaurantSchedule;
import com.bobjool.restaurant.domain.repository.RestaurantScheduleRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantScheduleRepositoryImpl
    extends JpaRepository<RestaurantSchedule, UUID>, RestaurantScheduleRepository, RestaurantScheduleRepositoryCustom {
}
