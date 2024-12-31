package com.bobjool.restaurant.infrastructure.repository;

import com.bobjool.restaurant.domain.entity.Restaurant;
import com.bobjool.restaurant.domain.repository.RestaurantRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RestaurantRepositoryImpl
    extends JpaRepository<Restaurant, UUID>, RestaurantRepository, RestaurantRepositoryCustom {
}
