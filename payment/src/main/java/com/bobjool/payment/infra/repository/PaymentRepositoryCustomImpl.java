package com.bobjool.payment.infra.repository;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.payment.domain.entity.Payment;
import com.bobjool.payment.domain.enums.PaymentStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.List;

import static com.bobjool.payment.domain.entity.QPayment.*;

@RequiredArgsConstructor
public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Payment> search(Long userId, PaymentStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        BooleanBuilder booleanBuilder = toBooleanBuilder(userId, status, startDate, endDate);
        List<Payment> contents = queryFactory.selectFrom(payment)
                .where(booleanBuilder)
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory.select(payment.count())
                .from(payment)
                .where(booleanBuilder)
                .fetchOne();

        return PageableExecutionUtils.getPage(contents, pageable, () -> totalCount);
    }

    private BooleanBuilder toBooleanBuilder(Long userId, PaymentStatus status, LocalDate startDate, LocalDate endDate) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(eqUserId(userId));
        booleanBuilder.and(eqStatus(status));
        booleanBuilder.and(afterStartDate(startDate));
        booleanBuilder.and(beforeEndDate(endDate));
        return booleanBuilder;
    }

    private BooleanExpression eqUserId(Long userId) {
        return userId != null ? payment.userId.eq(userId) : null;
    }

    private BooleanExpression eqStatus(PaymentStatus status) {
        return status != null ? payment.status.eq(status) : null;
    }

    private BooleanExpression afterStartDate(LocalDate startDate) {
        return startDate != null ? payment.createdAt.goe(startDate.atStartOfDay()) : null;
    }

    private BooleanExpression beforeEndDate(LocalDate endDate) {
        return endDate != null ? payment.createdAt.loe(endDate.atTime(23, 59, 59)) : null;
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
            case "createdAt" -> payment.createdAt;
            case "updatedAt" -> payment.updatedAt;
            case "userId" -> payment.userId;
            case "status" -> payment.status;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_SORT_TYPE);
        };
    }
}
