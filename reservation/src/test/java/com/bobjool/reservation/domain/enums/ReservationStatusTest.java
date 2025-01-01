package com.bobjool.reservation.domain.enums;

import com.bobjool.common.exception.BobJoolException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationStatusTest {

    @DisplayName("유효한 예약 상태 문자열로 ReservationStatus 를 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "COMPLETE", "CHECK_IN", "CANCEL", "NO_SHOW"})
    void of_whenValidStringRequest(String request) {
        // given & when
        ReservationStatus reservationStatus = ReservationStatus.of(request);

        // then
        assertThat(reservationStatus.name()).isEqualTo(request);
    }

    @DisplayName("유효하지 않은 예약 상태 문자열로 ReservationStatus 를 생성 시 예외가 발생한다.")
    @Test
    void of_whenInvalidStringRequest() {
        // given
        String invalidType = "INVALID";

        // when & then
        assertThatThrownBy(() -> PaymentStatus.of(invalidType))
                .isInstanceOf(BobJoolException.class)
                .hasMessage("지원하지 않는 결제 상태입니다.");
    }

}