package com.bobjool.payment.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentStatusTest {

    @ParameterizedTest
    @DisplayName("유효한 결제 상태 문자열로 PaymentStatus 를 생성한다.")
    @ValueSource(strings = {"PENDING", "COMPLETE", "FAIL", "REFUND"})
    void of_whenValidStringRequest(String request) {
        // given & when
        PaymentStatus paymentStatus = PaymentStatus.of(request);

        // then
        assertThat(paymentStatus.name()).isEqualTo(request);
    }

    @Test
    @DisplayName("유효하지 않은 결제 상태 문자열로 PaymentStatus 를 생성 시 예외가 발생한다.")
    void of_whenInvalidStringRequest() {
        // given
        String invalidType = "INVALID";

        // when & then
        assertThatThrownBy(() -> PaymentStatus.of(invalidType))
                .isInstanceOf(BobJoolException.class)
                .hasMessage("지원하지 않는 결제 상태입니다.");
    }

}