package com.bobjool.restaurant.infrastructure.repository.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepositoryCustom {

  /**
   * QueryDSl 인터페이스
   * */
  Page<Restaurant> findRestaurantPageByDeletedAtIsNull(
      String name, String region, String addressDetail, String description, Pageable pageable
  );
}
