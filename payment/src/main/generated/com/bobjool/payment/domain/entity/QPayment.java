package com.bobjool.payment.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = 753707545L;

    public static final QPayment payment = new QPayment("payment");

    public final com.bobjool.common.domain.entity.QBaseEntity _super = new com.bobjool.common.domain.entity.QBaseEntity(this);

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

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

    public final EnumPath<com.bobjool.payment.domain.enums.PaymentMethod> method = createEnum("method", com.bobjool.payment.domain.enums.PaymentMethod.class);

    public final EnumPath<com.bobjool.payment.domain.enums.PgName> pgName = createEnum("pgName", com.bobjool.payment.domain.enums.PgName.class);

    public final ComparablePath<java.util.UUID> reservationId = createComparable("reservationId", java.util.UUID.class);

    public final EnumPath<com.bobjool.payment.domain.enums.PaymentStatus> status = createEnum("status", com.bobjool.payment.domain.enums.PaymentStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPayment(String variable) {
        super(Payment.class, forVariable(variable));
    }

    public QPayment(Path<? extends Payment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayment(PathMetadata metadata) {
        super(Payment.class, metadata);
    }

}

