package com.bobjool.restaurant.domain.entity.restaurant;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRestaurant is a Querydsl query type for Restaurant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurant extends EntityPathBase<Restaurant> {

    private static final long serialVersionUID = -3519904L;

    public static final QRestaurant restaurant = new QRestaurant("restaurant");

    public final com.bobjool.common.domain.entity.QBaseEntity _super = new com.bobjool.common.domain.entity.QBaseEntity(this);

    public final TimePath<java.time.LocalTime> closeTime = createTime("closeTime", java.time.LocalTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final BooleanPath isQueue = createBoolean("isQueue");

    public final BooleanPath isReservation = createBoolean("isReservation");

    public final TimePath<java.time.LocalTime> openTime = createTime("openTime", java.time.LocalTime.class);

    public final StringPath restaurantAddressDetail = createString("restaurantAddressDetail");

    public final EnumPath<RestaurantCategory> restaurantCategory = createEnum("restaurantCategory", RestaurantCategory.class);

    public final StringPath restaurantDescription = createString("restaurantDescription");

    public final StringPath restaurantName = createString("restaurantName");

    public final StringPath restaurantPhone = createString("restaurantPhone");

    public final EnumPath<RestaurantRegion> restaurantRegion = createEnum("restaurantRegion", RestaurantRegion.class);

    public final NumberPath<Integer> restaurantVolume = createNumber("restaurantVolume", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRestaurant(String variable) {
        super(Restaurant.class, forVariable(variable));
    }

    public QRestaurant(Path<? extends Restaurant> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurant(PathMetadata metadata) {
        super(Restaurant.class, metadata);
    }

}

