package com.bobjool.restaurant.domain.repository;

import com.bobjool.restaurant.domain.entity.Restaurant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepository {

  Optional<Restaurant> findById(UUID id);

  Optional<Restaurant> findByRestaurantName(String restaurantName);

  Optional<Restaurant> findByRestaurantPhone(String restaurantPhone);

  Optional<Restaurant> findByRestaurantAddressDetail(String AddressDetail);


  Restaurant save(Restaurant restaurant);

  // todo JpaRepository 에 있는 시그니쳐와 정확하게 동일해야 합니다.

  Page<Restaurant> findAllByIsDeletedFalse(Pageable pageable);

  // todo 직접 만든 메서드는 테스트 해봐야 합니다.

}
