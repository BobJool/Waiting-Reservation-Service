package com.bobjool.restaurant.infrastructure.repository.restaurant;

import com.bobjool.restaurant.domain.entity.restaurant.QRestaurant;
import com.bobjool.restaurant.domain.entity.restaurant.Restaurant;
import com.bobjool.restaurant.domain.entity.restaurant.RestaurantRegion;
import com.querydsl.core.BooleanBuilder;
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

  //전체 키워드 검색
  public Page<Restaurant> findByRestaurantKeyword(
      String keyword, Pageable pageable
  ) {
    QRestaurant restaurant = QRestaurant.restaurant;

    BooleanExpression keywordCondition = createKeywordCondition(keyword);


    // QueryDSL의 페이징 지원
    List<Restaurant> results = queryFactory
        .selectFrom(restaurant)
        .where(
            restaurantDeletedAtIsNull(),
            keywordCondition
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = queryFactory
        .select(restaurant.count())
        .from(restaurant)
        .where(
            restaurantDeletedAtIsNull(),
            keywordCondition
        )
        .fetchOne();

    return new PageImpl<>(results, pageable, total);
  }

  //상세 검색
  public Page<Restaurant> findByRestaurantDetail(
      String name, String region, String addressDetail, String description, Pageable pageable
  ) {
    QRestaurant restaurant = QRestaurant.restaurant;
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

    List<Restaurant> results = queryFactory
        .selectFrom(restaurant)
        .where(
            restaurantDeletedAtIsNull(),
            booleanBuilder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = queryFactory
        .select(restaurant.count())
        .from(restaurant)
        .where(
            restaurantDeletedAtIsNull(),
          booleanBuilder)
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

  // 검색 조건 생성 메서드(키워드 검색)
  private BooleanExpression createKeywordCondition(String keyword) {
    QRestaurant restaurant = QRestaurant.restaurant;

    if (keyword == null || keyword.isEmpty()) {
      return restaurant.isNotNull(); // 기본 조건을 추가
    }

    return restaurant.restaurantName.containsIgnoreCase(keyword)
        .or(restaurant.restaurantDescription.containsIgnoreCase(keyword))
        .or(restaurant.restaurantAddressDetail.containsIgnoreCase(keyword));
  }
}
