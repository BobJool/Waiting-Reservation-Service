package com.bobjool.infrastructure.repository.user;

import com.bobjool.domain.entity.QUser;
import com.bobjool.domain.entity.User;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> search(Pageable pageable) {
        QUser user = QUser.user;

        QueryResults<User> results = queryFactory
                .selectFrom(user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<User> users = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(users, pageable, total);
    }
}