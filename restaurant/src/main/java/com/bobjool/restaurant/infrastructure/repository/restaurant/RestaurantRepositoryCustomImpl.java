package com.bobjool.restaurant.infrastructure.repository.restaurant;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

  /**
   * QueryDSL 구현체
  * */

  private final JPAQueryFactory queryFactory;

}
