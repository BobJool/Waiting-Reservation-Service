package com.bobjool.notification.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

/**
 * QueryDSL 구현체
 */
@RequiredArgsConstructor
public class AiRepositoryCustomImpl implements AiRepositoryCustom {
    private final JPAQueryFactory queryFactory;
}
