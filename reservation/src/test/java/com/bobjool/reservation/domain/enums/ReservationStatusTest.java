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

    @DisplayName("canCancel - PENDING, COMPLETE, CANCEL 에서는 예약 취소할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "COMPLETE", "CANCEL"})
    void canCancel_whenValidStringRequest(String request) {
        // given - 유효한 상태값이 주어졌을 때
        ReservationStatus reservationStatus = ReservationStatus.of(request);

        // when
        boolean result = reservationStatus.canCancel();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("canCancel - CHECK_IN, NO_SHOW 에서는 예약 취소가 불가능하다.")
    @ParameterizedTest
    @ValueSource(strings = {"CHECK_IN", "NO_SHOW"})
    void canCancel_whenInvalidStringRequest(String request) {
        // given - 유효한 상태값이 주어졌을 때
        ReservationStatus reservationStatus = ReservationStatus.of(request);

        // when
        boolean result = reservationStatus.canCancel();

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("canNotCancel - !canCancel - PENDING, COMPLETE, CANCEL 은 false 반환")
    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "COMPLETE", "CANCEL"})
    void canNotCancel_whenValidStringRequest(String request) {
        // given - 유효한 상태값이 주어졌을 때
        ReservationStatus reservationStatus = ReservationStatus.of(request);

        // when
        boolean result = reservationStatus.canNotCancel();

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("canNotCancel - !canCancel - CHECK_IN, NO_SHOW 은 true 반환")
    @ParameterizedTest
    @ValueSource(strings = {"CHECK_IN", "NO_SHOW"})
    void canNotCancel_whenInvalidStringRequest(String request) {
        // given - 유효한 상태값이 주어졌을 때
        ReservationStatus reservationStatus = ReservationStatus.of(request);

        // when
        boolean result = reservationStatus.canNotCancel();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("isPending - 상태가 PENDING 이면 true 를 반환한다")
    @Test
    void isPending_returnsTrueWhenStatusIsPending() {
        // given
        ReservationStatus status = ReservationStatus.PENDING;

        // when
        boolean result = status.isPending();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("isPending - 상태가 PENDING 이 아니면 false 를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"COMPLETE", "CHECK_IN", "CANCEL", "NO_SHOW", "FAIL"})
    void isPending_returnsFalseWhenStatusIsNotPending(String statusString) {
        // given
        ReservationStatus status = ReservationStatus.of(statusString);

        // when
        boolean result = status.isPending();

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("isNotPending - 상태가 PENDING 이 아니면 true 를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"COMPLETE", "CHECK_IN", "CANCEL", "NO_SHOW", "FAIL"})
    void isNotPending_returnsTrueWhenStatusIsNotPending(String statusString) {
        // given
        ReservationStatus status = ReservationStatus.of(statusString);

        // when
        boolean result = status.isNotPending();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("isNotPending - 상태가 PENDING 이면 false 를 반환한다")
    @Test
    void isNotPending_returnsFalseWhenStatusIsPending() {
        // given
        ReservationStatus status = ReservationStatus.PENDING;

        // when
        boolean result = status.isNotPending();

        // then
        assertThat(result).isFalse();
    }
}