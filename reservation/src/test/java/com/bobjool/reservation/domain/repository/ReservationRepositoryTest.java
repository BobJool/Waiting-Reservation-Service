package com.bobjool.reservation.domain.repository;

import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    Long userId1;
    Long userId2;
    Long userId3;

    UUID restaurantId1;
    UUID restaurantId2;

    UUID restaurantScheduleId1;
    UUID restaurantScheduleId2;
    UUID restaurantScheduleId3;
    UUID restaurantScheduleId4;

    @BeforeEach
    void setup() {
        userId1 = 1L;
        userId2 = 2L;
        userId3 = 3L;

        restaurantId1 = UUID.fromString("1252d33d-bd4e-46dd-b777-448f032ce833");
        restaurantId2 = UUID.fromString("da861b0e-012c-4501-b266-83b1c6a56096");

        restaurantScheduleId1 = UUID.fromString("1252d33d-bd4e-46dd-b777-448f032ce001");
        restaurantScheduleId2 = UUID.fromString("1252d33d-bd4e-46dd-b777-448f032ce002");
        restaurantScheduleId3 = UUID.fromString("da861b0e-012c-4501-b266-83b1c6a56003");
        restaurantScheduleId4 = UUID.fromString("da861b0e-012c-4501-b266-83b1c6a56004");
    }

    @DisplayName("search - userId 가 주어졌을 때")
    @Test
    void search_whenUserId() {
        // given - 10개의 reservation 이 주어졌을 때, userId가 주어졌을 때,
        List<Reservation> reservations = createDummyReservations();
        reservationRepository.saveAll(reservations);
        Pageable pageable = PageRequest.of(0, 20);
        Long givenUserId = userId1;

        // when - userId를 조건으로 호출하면
        Page<Reservation> result = reservationRepository.search(givenUserId, null, null, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(4) // userId1에 해당하는 데이터는 총 4개
                .extracting("userId", "restaurantId", "restaurantScheduleId", "status", "guestCount")
                .containsExactlyInAnyOrder(
                        tuple(userId1, restaurantId1, restaurantScheduleId1, ReservationStatus.PENDING, 2),
                        tuple(userId1, restaurantId2, restaurantScheduleId4, ReservationStatus.NO_SHOW, 8),
                        tuple(userId1, restaurantId1, restaurantScheduleId3, ReservationStatus.CANCEL, 6),
                        tuple(userId1, restaurantId2, restaurantScheduleId2, ReservationStatus.COMPLETE, 4)
                );
    }



    @DisplayName("search - restaurantId 가 주어졌을 때")
    @Test
    void search_whenRestaurantId() {
        // given - 10개의 reservation 이 주어졌을 때, restaurantId 가 주어졌을 때,
        List<Reservation> reservations = createDummyReservations();
        reservationRepository.saveAll(reservations);
        Pageable pageable = PageRequest.of(0, 20);
        UUID givenRestaurantId = restaurantId1;

        // when - restaurantId 를 조건으로 호출하면
        Page<Reservation> result = reservationRepository.search(null, givenRestaurantId, null, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(5) // restaurantId 에 해당하는 데이터는 총 4개
                .extracting("userId", "restaurantId", "restaurantScheduleId", "status", "guestCount")
                .containsExactlyInAnyOrder(
                        tuple(userId1, restaurantId1, restaurantScheduleId1, ReservationStatus.PENDING, 2),
                        tuple(userId3, restaurantId1, restaurantScheduleId3, ReservationStatus.CANCEL, 6),
                        tuple(userId2, restaurantId1, restaurantScheduleId1, ReservationStatus.PENDING, 2),
                        tuple(userId1, restaurantId1, restaurantScheduleId3, ReservationStatus.CANCEL, 6),
                        tuple(userId3, restaurantId1, restaurantScheduleId1, ReservationStatus.PENDING, 2)
                );
    }

    @DisplayName("search - restaurantScheduleId 가 주어졌을 때")
    @Test
    void search_whenRestaurantScheduleId() {
        // given - 10개의 reservation 이 주어졌을 때, restaurantScheduleId 가 주어졌을 때,
        List<Reservation> reservations = createDummyReservations();
        reservationRepository.saveAll(reservations);
        Pageable pageable = PageRequest.of(0, 20);
        UUID givenRestaurantScheduleId = restaurantScheduleId1;

        // when - restaurantScheduleId 를 조건으로 호출하면
        Page<Reservation> result = reservationRepository.search(null, null, givenRestaurantScheduleId, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(3) // restaurantScheduleId 에 해당하는 데이터는 총 3개
                .extracting("userId", "restaurantId", "restaurantScheduleId", "status", "guestCount")
                .containsExactlyInAnyOrder(
                        tuple(userId1, restaurantId1, restaurantScheduleId1, ReservationStatus.PENDING, 2),
                        tuple(userId2, restaurantId1, restaurantScheduleId1, ReservationStatus.PENDING, 2),
                        tuple(userId3, restaurantId1, restaurantScheduleId1, ReservationStatus.PENDING, 2)
                );
    }

    @DisplayName("search - status 가 주어졌을 때")
    @Test
    void search_whenStatus() {
        // given - 10개의 reservation 이 주어졌을 때, status 가 주어졌을 때,
        List<Reservation> reservations = createDummyReservations();
        reservationRepository.saveAll(reservations);
        Pageable pageable = PageRequest.of(0, 20);
        ReservationStatus status = ReservationStatus.COMPLETE;

        // when - status 를 조건으로 호출하면
        Page<Reservation> result = reservationRepository.search(null, null, null, status, pageable);

        // then
        assertThat(result.getContent()).hasSize(3) // status 에 해당하는 데이터는 총 3개
                .extracting("userId", "restaurantId", "restaurantScheduleId", "status", "guestCount")
                .containsExactlyInAnyOrder(
                        tuple(userId2, restaurantId2, restaurantScheduleId2, ReservationStatus.COMPLETE, 4),
                        tuple(userId3, restaurantId2, restaurantScheduleId2, ReservationStatus.COMPLETE, 4),
                        tuple(userId1, restaurantId2, restaurantScheduleId2, ReservationStatus.COMPLETE, 4)
                );
    }

    private List<Reservation> createDummyReservations() {
        List<Reservation> reservations = new ArrayList<>();

        ReservationStatus[] statuses = {
                ReservationStatus.PENDING,
                ReservationStatus.COMPLETE,
                ReservationStatus.CANCEL,
                ReservationStatus.NO_SHOW
        };
        int[] guestCounts = {2, 4, 6, 8};

        // 10개의 더미 데이터 생성
        for (int i = 0; i < 10; i++) {
            // 순환적으로 userId, restaurantId, restaurantScheduleId, status, guestCount 선택
            Long userId = (i % 3 == 0) ? userId1 : (i % 3 == 1) ? userId2 : userId3;
            UUID restaurantId = (i % 2 == 0) ? restaurantId1 : restaurantId2;
            UUID restaurantScheduleId = switch (i % 4) {
                case 0 -> restaurantScheduleId1;
                case 1 -> restaurantScheduleId2;
                case 2 -> restaurantScheduleId3;
                default -> restaurantScheduleId4;
            };
            ReservationStatus status = statuses[i % statuses.length];
            int guestCount = guestCounts[i % guestCounts.length];

            // Reservation 객체 생성 및 리스트에 추가
            Reservation reservation = Reservation.create(userId, restaurantId, restaurantScheduleId, status, guestCount,
                    LocalDate.now(), LocalTime.now());
            reservations.add(reservation);
        }

        return reservations;
    }


}