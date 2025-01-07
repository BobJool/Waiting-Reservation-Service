package com.bobjool.restaurant.domain.entity.restaurantSchedule;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRestaurantSchedule is a Querydsl query type for RestaurantSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurantSchedule extends EntityPathBase<RestaurantSchedule> {

    private static final long serialVersionUID = -1515961344L;

    public static final QRestaurantSchedule restaurantSchedule = new QRestaurantSchedule("restaurantSchedule");

    public final com.bobjool.common.domain.entity.QBaseEntity _super = new com.bobjool.common.domain.entity.QBaseEntity(this);

    public final BooleanPath available = createBoolean("available");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Integer> currentCapacity = createNumber("currentCapacity", Integer.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final NumberPath<Integer> maxCapacity = createNumber("maxCapacity", Integer.class);

    public final ComparablePath<java.util.UUID> restaurantId = createComparable("restaurantId", java.util.UUID.class);

    public final NumberPath<Integer> tableNumber = createNumber("tableNumber", Integer.class);

    public final TimePath<java.time.LocalTime> timeSlot = createTime("timeSlot", java.time.LocalTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRestaurantSchedule(String variable) {
        super(RestaurantSchedule.class, forVariable(variable));
    }

    public QRestaurantSchedule(Path<? extends RestaurantSchedule> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurantSchedule(PathMetadata metadata) {
        super(RestaurantSchedule.class, metadata);
    }

}

