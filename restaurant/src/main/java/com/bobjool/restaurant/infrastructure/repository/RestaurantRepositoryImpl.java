package com.bobjool.restaurant.infrastructure.repository;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.repository.RestaurantRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepositoryImpl
    extends JpaRepository<Restaurant, UUID>, RestaurantRepository, RestaurantRepositoryCustom {
}
