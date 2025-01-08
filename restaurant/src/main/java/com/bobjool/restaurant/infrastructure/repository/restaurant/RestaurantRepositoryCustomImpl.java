package com.bobjool.restaurant.infrastructure.repository.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.QRestaurant;
import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

  /**
   * QueryDSL 구현체
  * */

  private final JPAQueryFactory queryFactory;

//  public Page<Restaurant> findRestaurantPageByDeletedAtIsNull(
//      String name, String region, String addressDetail, String description, Pageable pageable
//  ) {
//    QRestaurant restaurant = QRestaurant.restaurant; // Q 클래스를 가져옵니다.
//
//
//    // QueryDSL의 페이징 지원
//    List<Restaurant> content = queryFactory
//        .selectFrom(restaurant)
//        .where(
//            restaurantDeletedAtIsNull(),
//            restaurantRegionEq(region),
//            restaurantNameLike(name),
//            restaurantAddressDetailLike(addressDetail),
//            restaurantDescriptionLike(description)
//        )
//        .offset(pageable.getOffset())
//        .limit(pageable.getPageSize())
//        .fetch();
//
//    long total = queryFactory
//        .select(restaurant.count())
//        .from(restaurant)
//        .where(
//            restaurantDeletedAtIsNull(),
//            restaurantRegionEq(region),
//            restaurantNameLike(name),
//            restaurantAddressDetailLike(addressDetail),
//            restaurantDescriptionLike(description)
//        )
//        .fetchOne();
//
//    return new PageImpl<>(content, pageable, total);
//  }

  public Page<Restaurant> findRestaurantPageByDeletedAtIsNull(
      String name, String region, String addressDetail, String description, Pageable pageable
  ) {
    QRestaurant restaurant = QRestaurant.restaurant; // Q 클래스를 가져옵니다.
    BooleanBuilder booleanBuilder = new BooleanBuilder();

    if (region != null) {
      booleanBuilder.and(restaurantRegionEq(region));
    }
    if (name != null) {
      booleanBuilder.and(restaurantNameLike(name));
    }
    if (addressDetail != null) {
      booleanBuilder.and(restaurantAddressDetailLike(addressDetail));
    }
    if (description != null) {
      booleanBuilder.and(restaurantDescriptionLike(description));
    }

    // QueryDSL의 페이징 지원
    List<Restaurant> results = queryFactory
        .selectFrom(restaurant)
        .where(booleanBuilder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = queryFactory
        .select(restaurant.count())
        .from(restaurant)
        .where(booleanBuilder)
        .fetchOne();

    return new PageImpl<>(results, pageable, total);
  }

  // 삭제되지 않은 레스토랑 조건
  private BooleanExpression restaurantDeletedAtIsNull() {
    return QRestaurant.restaurant.deletedAt.isNull();
  }

  // region 검색 조건 (정확히 일치)
  private BooleanExpression restaurantRegionEq(String region) {
    return region != null ? QRestaurant.restaurant.restaurantRegion.eq(RestaurantRegion.valueOf(region)) : null;
  }

  // name 검색 조건 (like 검색)
  private BooleanExpression restaurantNameLike(String name) {
    return name != null ? QRestaurant.restaurant.restaurantName.containsIgnoreCase(name) : null;
  }

  // addressDetail 검색 조건 (like 검색)
  private BooleanExpression restaurantAddressDetailLike(String addressDetail) {
    return addressDetail != null ? QRestaurant.restaurant.restaurantAddressDetail.containsIgnoreCase(addressDetail) : null;
  }

  // description 검색 조건 (like 검색)
  private BooleanExpression restaurantDescriptionLike(String description) {
    return description != null ? QRestaurant.restaurant.restaurantDescription.containsIgnoreCase(description) : null;
  }
}
