package com.bobjool.restaurant.infrastructure.repository.restaurantSchedule;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantScheduleRepositoryCustomImpl implements RestaurantScheduleRepositoryCustom {

  /**
   * QueryDSL 구현체
  * */

  private final JPAQueryFactory queryFactory;

}
