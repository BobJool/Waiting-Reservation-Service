package com.bobjool.reservation.domain.entity;

import com.bobjool.reservation.domain.enums.PaymentMethod;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import com.bobjool.reservation.domain.enums.PgName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @DisplayName("create - 성공")
    @Test
    void create_success() {
        // given - 아래의 조건이 주어졌을 때
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 1000;
        PaymentStatus status = PaymentStatus.COMPLETE;
        PaymentMethod method = PaymentMethod.CARD;
        PgName toss = PgName.TOSS;

        // when - 테스트하고자 하는 메서드를 수행하면!
        Payment payment = Payment.create(reservationId, userId, amount, status, method, toss);

        // then - 결과물을 검증해본다.
        assertThat(payment.getReservationId()).isEqualTo(reservationId);
        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getStatus()).isEqualTo(status);
        assertThat(payment.getMethod()).isEqualTo(method);
        assertThat(payment.getPgName()).isEqualTo(toss);
        
        // JpaAuditing 을 안거쳤으니까 아래는 null 이 되는 게 맞다
        assertThat(payment.getId()).isNull();
        assertThat(payment.getIsDeleted()).isNull();
        assertThat(payment.getCreatedAt()).isNull();
        assertThat(payment.getUpdatedAt()).isNull();
    }

}