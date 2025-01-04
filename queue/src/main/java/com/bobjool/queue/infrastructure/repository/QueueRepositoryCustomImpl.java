package com.bobjool.queue.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bobjool.queue.domain.entity.QQueue;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryCustomImpl implements QueueRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	QQueue queue = QQueue.queue;

	@Override
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public Integer findMaxPositionByRestaurantIdWithLock(UUID restaurantId) {
		Integer maxPosition = queryFactory
			.select(queue.position.max())
			.from(queue)
			.where(queue.restaurantId.eq(restaurantId)
				.and(queue.isDeleted.eq(false)))
			.setLockMode(LockModeType.PESSIMISTIC_WRITE)
			.fetchOne();
		return maxPosition != null ? maxPosition : 0;
	}

}
