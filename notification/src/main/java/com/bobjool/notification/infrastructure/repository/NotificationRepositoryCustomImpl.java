package com.bobjool.notification.infrastructure.repository;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.Notification;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;
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

import static com.bobjool.notification.domain.entity.QNotification.notification;
import static com.bobjool.notification.domain.entity.QTemplate.template1;

/**
 * QueryDSL 구현체
 */
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Notification> search(
            Long userId,
            String message,
            LocalDate date,
            BobjoolServiceType category,
            NotificationType action,
            NotificationChannel channel,
            Pageable pageable
    ) {
        BooleanBuilder builder = this.toBooleanBuilder(
                userId,
                message,
                date,
                category,
                action,
                channel
        );
        List<Notification> contents = queryFactory.selectFrom(notification)
                .innerJoin(notification.templateId, template1)
                .where(builder)
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(notification.count())
                .from(notification)
                .innerJoin(notification.templateId, template1)
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(
                contents,
                pageable,
                () -> totalCount
        );
    }

    private BooleanBuilder toBooleanBuilder(
            Long userId,
            String message,
            LocalDate date,
            BobjoolServiceType category,
            NotificationType action,
            NotificationChannel channel
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(eqDeletedAtIsNull());
        if (userId != null) builder.and(eqUserId(userId));
        if (message != null) builder.and(eqMessage(message));
        if (date != null) builder.and(eqDate(date));
        if (category != null) builder.and(eqServiceType(category));
        if (action != null) builder.and(eqType(action));
        if (channel != null) builder.and(eqChannel(channel));

        return builder;
    }

    private BooleanExpression eqDeletedAtIsNull() {
        return notification.deletedAt.isNull();
    }

    private BooleanExpression eqUserId(Long userId) {
        return userId != null ? notification.userId.eq(userId) : null;
    }

    private BooleanExpression eqMessage(String message) {
        return !message.isBlank() ? notification.message.containsIgnoreCase(message) : null;
    }

    private BooleanExpression eqDate(LocalDate date) {
        return date != null ? notification.createdAt.between(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        ) : null;
    }

    private BooleanExpression eqServiceType(BobjoolServiceType category) {
        return category != null ? template1.serviceType.eq(category) : null;
    }

    private BooleanExpression eqType(NotificationType type) {
        return type != null ? template1.type.eq(type) : null;
    }

    private BooleanExpression eqChannel(NotificationChannel channel) {
        return channel != null ? template1.channel.eq(channel) : null;
    }

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

    private ComparableExpressionBase<?> getSortPath(String property) {
        return switch (property) {
            case "createdAt" -> notification.createdAt;
            case "userId" -> notification.userId;
            case "status" -> notification.status;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_SORT_TYPE);
        };
    }
}
