package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.application.dto.reservation.ReservationCreateDto;
import com.bobjool.reservation.application.dto.reservation.ReservationResDto;
import com.bobjool.reservation.application.dto.reservation.ReservationSearchDto;
import com.bobjool.reservation.application.dto.reservation.ReservationUpdateDto;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import com.bobjool.reservation.domain.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    /**
     * createReservation - 2개 테스트
     * */
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

    /**
     * updateReservationStatus - 3개 테스트
     * */
    @DisplayName("updateReservationStatus - 성공적으로 상태를 업데이트한다.")
    @Test
    void updateReservationStatus_whenValidInput() {
        // given - PENDING 상태의 예약이 존재할 때
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = Reservation.create(
                12345L,
                UUID.randomUUID(),
                UUID.randomUUID(),
                5
        );
        reservationRepository.save(reservation);

        ReservationUpdateDto updateDto = new ReservationUpdateDto("COMPLETE");

        // when - 상태 업데이트 호출
        var response = reservationService.updateReservationStatus(updateDto, reservation.getId());

        // then - 상태가 업데이트되었는지 확인
        assertThat(response.status()).isEqualTo("COMPLETE");

        // and - 데이터베이스에서도 상태가 반영되었는지 확인
        Reservation updatedReservation = reservationRepository.findById(reservation.getId()).orElse(null);
        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.COMPLETE);
    }

    @DisplayName("updateReservationStatus - 예약이 존재하지 않을 경우 BobJoolException 발생")
    @Test
    void updateReservationStatus_whenReservationNotFound() {
        // given - 존재하지 않는 예약 ID와 상태값이 주어졌을 때
        UUID nonExistentReservationId = UUID.randomUUID();
        ReservationUpdateDto updateDto = new ReservationUpdateDto("COMPLETE");

        // when & then - 예외 발생 확인
        assertThatThrownBy(() -> reservationService.updateReservationStatus(updateDto, nonExistentReservationId))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }

    @DisplayName("updateReservationStatus - 잘못된 상태값으로 BobJoolException 발생")
    @Test
    void updateReservationStatus_whenInvalidStatus() {
        // given - 예약이 존재하지만 잘못된 상태값이 주어졌을 때
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = Reservation.create(
                12345L,
                UUID.randomUUID(),
                UUID.randomUUID(),
                5
        );
        reservationRepository.save(reservation);

        ReservationUpdateDto updateDto = new ReservationUpdateDto("INVALID_STATUS");

        // when & then - 예외 발생 확인
        assertThatThrownBy(() -> reservationService.updateReservationStatus(updateDto, reservation.getId()))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.UNSUPPORTED_RESERVATION_STATUS.getMessage());
    }

    @DisplayName("cancelReservation - 성공적으로 예약을 취소한다.")
    @Test
    void cancelReservation_success() {
        // given - PENDING 상태의 예약이 존재할 때
        Reservation reservation = Reservation.create(
                12345L,
                UUID.randomUUID(),
                UUID.randomUUID(),
                5
        );
        reservationRepository.save(reservation);

        // when - cancelReservation 호출
        var response = reservationService.cancelReservation(reservation.getId());

        // then - 상태가 CANCEL 로 변경되었는지 확인
        assertThat(response.status()).isEqualTo("CANCEL");

        // and - 데이터베이스에서도 상태가 반영되었는지 확인
        Reservation canceledReservation = reservationRepository.findById(reservation.getId()).orElse(null);
        assertThat(canceledReservation.getStatus()).isEqualTo(ReservationStatus.CANCEL);
    }

    @DisplayName("cancelReservation - 예약이 존재하지 않을 경우 BobJoolException 발생")
    @Test
    void cancelReservation_whenReservationNotFound() {
        // given - 존재하지 않는 예약 ID
        UUID nonExistentReservationId = UUID.randomUUID();

        // when & then - 예외 발생 확인
        assertThatThrownBy(() -> reservationService.cancelReservation(nonExistentReservationId))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }

    @DisplayName("cancelReservation - 취소할 수 없는 상태일 경우 BobJoolException 발생")
    @ParameterizedTest
    @ValueSource(strings = {"CHECK_IN", "NO_SHOW"})
    void cancelReservation_whenCannotCancel(String status) {
        // given - 취소 불가능한 상태의 예약이 존재할 때
        ReservationStatus reservationStatus = ReservationStatus.of(status);
        Reservation reservation = Reservation.create(
                12345L,
                UUID.randomUUID(),
                UUID.randomUUID(),
                reservationStatus,
                5
        );
        reservationRepository.save(reservation);

        // when & then - 예외 발생 확인
        assertThatThrownBy(() -> reservationService.cancelReservation(reservation.getId()))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.CANNOT_CANCEL.getMessage());
    }

    @DisplayName("getReservation - 성공적으로 예약 정보를 반환한다.")
    @Test
    void getReservation_whenReservationExists() {
        // given - 예약이 존재할 때
        Reservation reservation = Reservation.create(
                12345L,
                UUID.randomUUID(),
                UUID.randomUUID(),
                5
        );
        reservationRepository.save(reservation);

        // when - getReservation 호출
        ReservationResDto response = reservationService.getReservation(reservation.getId());

        // then - 반환된 예약 정보가 올바른지 확인
        assertThat(response.reservationId()).isEqualTo(reservation.getId());
        assertThat(response.userId()).isEqualTo(reservation.getUserId());
        assertThat(response.restaurantId()).isEqualTo(reservation.getRestaurantId());
        assertThat(response.restaurantScheduleId()).isEqualTo(reservation.getRestaurantScheduleId());
        assertThat(response.guestCount()).isEqualTo(reservation.getGuestCount());
        assertThat(response.status()).isEqualTo(reservation.getStatus().name());
    }

    @DisplayName("getReservation - 예약이 존재하지 않을 경우 BobJoolException 발생")
    @Test
    void getReservation_whenReservationNotFound() {
        // given - 존재하지 않는 예약 ID
        UUID nonExistentReservationId = UUID.randomUUID();

        // when & then - 예외 발생 확인
        assertThatThrownBy(() -> reservationService.getReservation(nonExistentReservationId))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }

    @DisplayName("search - userId로 검색 시 성공적으로 결과를 반환한다.")
    @Test
    void search_whenUserIdProvided() {
        // given - 특정 userId를 가진 예약 데이터가 존재할 때
        Long userId = 12345L;
        UUID restaurantId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        Reservation reservation1 = Reservation.create(userId, restaurantId, scheduleId, 2);
        Reservation reservation2 = Reservation.create(userId, restaurantId, UUID.randomUUID(), 4);
        Reservation reservation3 = Reservation.create(67890L, restaurantId, scheduleId, 3); // 다른 userId
        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3));

        Pageable pageable = PageRequest.of(0, 10);
        ReservationSearchDto searchDto = new ReservationSearchDto(userId, null, null, null);

        // when - search 메서드 호출
        Page<ReservationResDto> result = reservationService.search(searchDto, pageable);

        // then - 해당 userId의 예약만 반환되는지 확인
        assertThat(result.getContent()).hasSize(2)
                .extracting("userId", "restaurantScheduleId", "guestCount")
                .containsExactlyInAnyOrder(
                        tuple(userId, scheduleId, 2),
                        tuple(userId, reservation2.getRestaurantScheduleId(), 4)
                );
    }

    @DisplayName("search - 잘못된 status 값으로 BobJoolException 발생")
    @Test
    void search_whenInvalidStatusProvided() {
        // given - 잘못된 status 값이 주어졌을 때
        String invalidStatus = "INVALID";
        ReservationSearchDto searchDto = new ReservationSearchDto(null, null, null, invalidStatus);
        Pageable pageable = PageRequest.of(0, 10);

        // when & then - 예외 발생 검증
        assertThatThrownBy(() -> reservationService.search(searchDto, pageable))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.UNSUPPORTED_RESERVATION_STATUS.getMessage());
    }


}
