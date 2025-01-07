package com.bobjool.queue.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QQueue is a Querydsl query type for Queue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQueue extends EntityPathBase<Queue> {

    private static final long serialVersionUID = 269462639L;

    public static final QQueue queue = new QQueue("queue");

    public final com.bobjool.common.domain.entity.QBaseEntity _super = new com.bobjool.common.domain.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Integer> delayCount = createNumber("delayCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final EnumPath<com.bobjool.queue.domain.enums.DiningOption> diningOption = createEnum("diningOption", com.bobjool.queue.domain.enums.DiningOption.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final NumberPath<Integer> member = createNumber("member", Integer.class);

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final ComparablePath<java.util.UUID> restaurantId = createComparable("restaurantId", java.util.UUID.class);

    public final EnumPath<com.bobjool.queue.domain.enums.QueueStatus> status = createEnum("status", com.bobjool.queue.domain.enums.QueueStatus.class);

    public final EnumPath<com.bobjool.queue.domain.enums.QueueType> type = createEnum("type", com.bobjool.queue.domain.enums.QueueType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QQueue(String variable) {
        super(Queue.class, forVariable(variable));
    }

    public QQueue(Path<? extends Queue> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQueue(PathMetadata metadata) {
        super(Queue.class, metadata);
    }

}

