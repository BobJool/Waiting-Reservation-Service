package com.bobjool.reservation.domain.entity;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.reservation.domain.enums.PaymentMethod;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import com.bobjool.reservation.domain.enums.PgName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @DisplayName("validateAmount - 양수이면 예외가 발생하지 않는다")
    @Test
    void validateAmount_whenPositive() {
        // given
        Integer amount = 1000;

        // when & then
        // validateAmount 메서드가 private 이므로 create 메서드를 호출하여 검증
        Payment.create(UUID.randomUUID(), 12345L, amount, PaymentStatus.PENDING, PaymentMethod.CARD, PgName.TOSS);

        // 예외가 발생하지 않으면 테스트 통과
    }

    @DisplayName("validateAmount - 음수이면 BobJoolException 이 발생한다")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void validateAmount_whenNegative(Integer amount) {
        // given - 결제 금액이 음수이면

        // when & then
        assertThatThrownBy(() ->
                Payment.create(UUID.randomUUID(), 12345L, amount, PaymentStatus.PENDING, PaymentMethod.CARD, PgName.TOSS)
        ).isInstanceOf(BobJoolException.class)
                .hasMessage("결제 금액은 양수여야 합니다."); // validateAmount 메서드에서 정의한 메시지와 일치해야 함
    }

}