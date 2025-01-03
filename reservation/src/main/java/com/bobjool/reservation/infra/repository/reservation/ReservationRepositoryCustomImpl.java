package com.bobjool.reservation.infra.repository.reservation;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.UUID;

import static com.bobjool.reservation.domain.entity.QReservation.reservation;

@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Reservation> search(Long userId, UUID restaurantId, UUID restaurantScheduleId,
                                    ReservationStatus status, Pageable pageable) {
        BooleanBuilder booleanBuilder = toBooleanBuilder(userId, restaurantId, restaurantScheduleId, status);
        List<Reservation> contents = queryFactory.selectFrom(reservation)
                .where(booleanBuilder)
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory.select(reservation.count())
                .from(reservation)
                .where(booleanBuilder)
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> totalCount);
    }

    private BooleanBuilder toBooleanBuilder(Long userId, UUID restaurantId,
                                            UUID restaurantScheduleId, ReservationStatus status) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(eqUserId(userId));
        booleanBuilder.and(eqRestaurantId(restaurantId));
        booleanBuilder.and(eqRestaurantScheduleId(restaurantScheduleId));
        booleanBuilder.and(eqStatus(status));
        return booleanBuilder;
    }

    private BooleanExpression eqUserId(Long userId) {
        return userId != null ? reservation.userId.eq(userId) : null;
    }

    private BooleanExpression eqRestaurantId(UUID restaurantId) {
        return restaurantId != null ? reservation.restaurantId.eq(restaurantId) : null;
    }

    private BooleanExpression eqRestaurantScheduleId(UUID restaurantScheduleId) {
        return restaurantScheduleId != null ? reservation.restaurantScheduleId.eq(restaurantScheduleId) : null;
    }

    private BooleanExpression eqStatus(ReservationStatus status) {
        return status != null ? reservation.status.eq(status) : null;
    }


    // Pageable 의 Sort 객체를 기반으로 QueryDSL OrderSpecifier 배열을 생성하는 메서드
    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    ComparableExpressionBase<?> sortPath = getSortPath(order.getProperty());
                    return new OrderSpecifier<>(
                            order.isAscending()
                                    ? com.querydsl.core.types.Order.ASC
                                    : com.querydsl.core.types.Order.DESC,
                            sortPath);
                })
                .toArray(OrderSpecifier[]::new);
    }

    // 정렬 기준
    private ComparableExpressionBase<?> getSortPath(String property) {
        return switch (property) {
            case "createdAt" -> reservation.createdAt;
            case "updatedAt" -> reservation.updatedAt;
            case "userId" -> reservation.userId;
            case "status" -> reservation.status;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_SORT_TYPE);
        };
    }
}
