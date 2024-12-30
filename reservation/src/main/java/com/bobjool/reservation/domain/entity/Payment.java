package com.bobjool.reservation.domain.entity;

import com.bobjool.common.domain.entity.BaseEntity;
import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.domain.enums.PaymentMethod;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import com.bobjool.reservation.domain.enums.PgName;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_payment")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "reservation_id", nullable = false)
    private UUID reservationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(name = "pg_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private PgName pgName;

    public static Payment create(UUID reservationId, Long userId, Integer amount,
                                 PaymentStatus status, PaymentMethod method, PgName pgName) {
        validateAmount(amount);
        return Payment.builder()
                .reservationId(reservationId)
                .userId(userId)
                .amount(amount)
                .status(status)
                .method(method)
                .pgName(pgName)
                .build();
    }

    public static Payment create(UUID reservationId, Long userId, Integer amount,
                          PaymentMethod method, PgName pgName) {
        return create(reservationId, userId, amount, PaymentStatus.COMPLETE, method, pgName);
    }

    /**
     * 도메인 규칙 - amount 는 양수여야 한다.
     * 금액(amount) 검증 메서드
     * @param amount 검증할 금액
     */
    private static void validateAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new BobJoolException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
    }
}
