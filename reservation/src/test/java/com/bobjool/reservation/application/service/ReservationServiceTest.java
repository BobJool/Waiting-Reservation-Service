package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.application.dto.reservation.ReservationCreateDto;
import com.bobjool.reservation.application.dto.reservation.ReservationResDto;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @DisplayName("createReservation - 성공적으로 예약을 생성하고 반환한다.")
    @Test
    void createReservation_whenValidInput() {
        // given - 적절한 요청이 들어왔을 때
        Long userId = 12345L;
        UUID restaurantId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        Integer guestCount = 5;

        ReservationCreateDto reservationCreateDto = new ReservationCreateDto(userId, restaurantId, scheduleId, guestCount);

        // when - reservationService.createReservation() 호출
        ReservationResDto response = reservationService.createReservation(reservationCreateDto);

        // then - 응답이 올바르게 반환된다.
        assertThat(response.reservationId()).isNotNull();
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.restaurantId()).isEqualTo(restaurantId);
        assertThat(response.restaurantScheduleId()).isEqualTo(scheduleId);
        assertThat(response.guestCount()).isEqualTo(guestCount);
        assertThat(response.status()).isEqualTo("PENDING");

        // and - Reservation 엔티티가 저장되었다.
        Reservation reservation = reservationRepository.findById(response.reservationId()).orElse(null);
        assertThat(reservation).isNotNull();
        assertThat(reservation.getUserId()).isEqualTo(userId);
        assertThat(reservation.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(reservation.getRestaurantScheduleId()).isEqualTo(scheduleId);
        assertThat(reservation.getGuestCount()).isEqualTo(guestCount);
        assertThat(reservation.getStatus().name()).isEqualTo("PENDING");
    }

    @DisplayName("createReservation - guestCount가 0 또는 음수일 경우 BobJoolException 발생")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void createReservation_whenInvalidGuestCount(Integer guestCount) {
        // given - guestCount가 0 또는 음수인 경우
        Long userId = 12345L;
        UUID restaurantId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();

        ReservationCreateDto reservationCreateDto = new ReservationCreateDto(userId, restaurantId, scheduleId, guestCount);

            // when & then - 예외 발생 검증
        assertThatThrownBy(() -> reservationService.createReservation(reservationCreateDto))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.INVALID_GUEST_COUNT.getMessage());
    }
}
