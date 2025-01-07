package com.bobjool.payment.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentMethodTest {

    @ParameterizedTest
    @DisplayName("유효한 결제 타입 문자열로 PaymentMethod 를 생성한다.")
    @ValueSource(strings = {"CARD", "CASH"})
    void of_whenValidStringRequest(String request) {
        // given & when
        PaymentMethod paymentMethod = PaymentMethod.of(request);

        // then
        assertThat(paymentMethod.name()).isEqualTo(request);
    }

    @Test
    @DisplayName("유효하지 않은 결제 타입 문자열로 PaymentMethod 를 생성 시 예외가 발생한다.")
    void of_whenInvalidStringRequest() {
        // given
        String invalidType = "INVALID";

        // when & then
        assertThatThrownBy(() -> PaymentMethod.of(invalidType))
                .isInstanceOf(BobJoolException.class)
                .hasMessage("지원하지 않는 결제 방식입니다.");
    }
}