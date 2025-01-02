package com.bobjool.restaurant.domain.repository;

import com.bobjool.restaurant.domain.entity.Restaurant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepository {

  Optional<Restaurant> findById(UUID id);

  Restaurant save(Restaurant restaurant);

  // todo JpaRepository 에 있는 시그니쳐와 정확하게 동일해야 합니다.
  <S extends Restaurant> List<S> saveAll(Iterable<S> entities);

  // todo 직접 만든 메서드는 테스트 해봐야 합니다.
//  Page<Restaurant> search(Long userId,
//      PaymentStatus status,
//      LocalDate startDate,
//      LocalDate endDate,
//      Pageable pageable);

}
