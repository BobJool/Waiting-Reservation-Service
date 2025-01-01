package com.bobjool.reservation.domain.entity;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    @DisplayName("create - 성공")
    @Test
    void create_success() {
        // given
        Long userId = 12345L;
        UUID restaurantId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        Integer guestCount = 5;
        ReservationStatus status = ReservationStatus.PENDING;

        // when
        Reservation reservation = Reservation.create(userId, restaurantId, scheduleId, status, guestCount);

        // then
        assertThat(reservation.getUserId()).isEqualTo(userId);
        assertThat(reservation.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(reservation.getRestaurantScheduleId()).isEqualTo(scheduleId);
        assertThat(reservation.getGuestCount()).isEqualTo(guestCount);
        assertThat(reservation.getStatus()).isEqualTo(status);

        // JpaAuditing 을 안거쳤으므로 아래 값은 null 이 되어야 함
        assertThat(reservation.getId()).isNull();
        assertThat(reservation.getCreatedAt()).isNull();
        assertThat(reservation.getUpdatedAt()).isNull();
    }

    @DisplayName("validateGuestCount - 양수이면 예외가 발생하지 않는다")
    @Test
    void validateGuestCount_whenPositive() {
        // given
        Integer guestCount = 5;

        // when & then
        Reservation.create(12345L, UUID.randomUUID(), UUID.randomUUID(), guestCount);

        // 예외가 발생하지 않으면 테스트 통과
    }

    @DisplayName("validateGuestCount - 음수이면 BobJoolException 이 발생한다")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void validateGuestCount_whenNegative(Integer guestCount) {
        // given

        // when & then
        assertThatThrownBy(() ->
                Reservation.create(12345L, UUID.randomUUID(), UUID.randomUUID(), guestCount)
        ).isInstanceOf(BobJoolException.class)
                .hasMessage("예약 인원수는 양수여야 합니다.");
    }

    @DisplayName("create - 기본 상태는 PENDING 이다")
    @Test
    void create_defaultStatusIsPending() {
        // given
        Long userId = 12345L;
        UUID restaurantId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        Integer guestCount = 3;

        // when
        Reservation reservation = Reservation.create(userId, restaurantId, scheduleId, guestCount);

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @DisplayName("updateStatus - status 를 변경한다.")
    @Test
    void updateStatus_success() {
        // given - COMPLETE 상태의 예약
        Long userId = 12345L;
        UUID restaurantId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        ReservationStatus completeStatus = ReservationStatus.COMPLETE;
        Integer guestCount = 3;

        Reservation reservation = Reservation.create(userId, restaurantId, scheduleId, completeStatus ,guestCount);

        // when - updateStatus 호출
        reservation.updateStatus(ReservationStatus.CHECK_IN);

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECK_IN);
    }

    @DisplayName("cancel - 예약 상태가 CANCEL 로 변경된다")
    @Test
    void cancel_success() {
        // given - PENDING 상태의 예약
        Long userId = 12345L;
        UUID restaurantId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        ReservationStatus pendingStatus = ReservationStatus.PENDING;
        Integer guestCount = 3;

        Reservation reservation = Reservation.create(userId, restaurantId, scheduleId, pendingStatus, guestCount);

        // when - cancel 호출
        reservation.cancel();

        // then - 상태가 CANCEL 로 변경되었는지 확인
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCEL);
    }

    @DisplayName("cancel - CHECK_IN 상태에서는 BobJoolException 발생")
    @Test
    void cancel_failWhenCheckIn() {
        // given - CHECK_IN 상태의 예약
        Long userId = 12345L;
        UUID restaurantId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        ReservationStatus checkInStatus = ReservationStatus.CHECK_IN;
        Integer guestCount = 3;

        Reservation reservation = Reservation.create(userId, restaurantId, scheduleId, checkInStatus, guestCount);

        // when & then - 예외 발생 확인
        assertThatThrownBy(reservation::cancel)
                .isInstanceOf(BobJoolException.class)
                .hasMessage("취소할 수 없는 예약 상태입니다.");
    }

    @DisplayName("cancel - NO_SHOW 상태에서는 BobJoolException 발생")
    @Test
    void cancel_failWhenNoShow() {
        // given - NO_SHOW 상태의 예약
        Long userId = 12345L;
        UUID restaurantId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        ReservationStatus noShowStatus = ReservationStatus.NO_SHOW;
        Integer guestCount = 3;

        Reservation reservation = Reservation.create(userId, restaurantId, scheduleId, noShowStatus, guestCount);

        // when & then - 예외 발생 확인
        assertThatThrownBy(reservation::cancel)
                .isInstanceOf(BobJoolException.class)
                .hasMessage("취소할 수 없는 예약 상태입니다.");
    }

}
