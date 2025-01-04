package com.bobjool.reservation.domain.service;

import com.bobjool.reservation.domain.entity.Payment;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static com.bobjool.reservation.domain.enums.PaymentMethod.CARD;
import static com.bobjool.reservation.domain.enums.PgName.TOSS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 단위 테스트로 진행
 * */
class ReservationPaymentDomainServiceTest {

    private ReservationPaymentDomainService reservationPaymentDomainService;

    @BeforeEach
    void setUp() {
        reservationPaymentDomainService = new ReservationPaymentDomainService();
    }

    @DisplayName("syncReservationWithPayment - Payment의 상태값이 COMPLETE 일 때")
    @Test
    void syncReservationWithPayment_whenPaymentStatusIsComplete() {
        // given - PENDING 상태의 Reservation이 주어졌을 때
        Reservation reservation = Reservation.create(
                1L,
                UUID.randomUUID(),
                UUID.randomUUID(),
                ReservationStatus.PENDING,
                4,
                LocalDate.now(),
                LocalTime.now()
        );

        // and - COMPLETE 상태의 Payment가 주어졌을 때
        Payment payment = Payment.create(UUID.randomUUID(), 1L, 10_000, PaymentStatus.COMPLETE, CARD, TOSS);

        // When
        reservationPaymentDomainService.syncReservationWithPayment(reservation, payment);

        // Then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.COMPLETE);
    }

    @DisplayName("syncReservationWithPayment - Payment의 상태값이 FAIL 일 때")
    @Test
    void syncReservationWithPayment_whenPaymentStatusIsFail() {
        // given - PENDING 상태의 Reservation 이 주어졌을 때
        Reservation reservation = Reservation.create(
                1L,
                UUID.randomUUID(),
                UUID.randomUUID(),
                ReservationStatus.PENDING,
                4,
                LocalDate.now(),
                LocalTime.now()
        );
        // and - FAIL 상태의 Payment가 주어졌을 때
        Payment payment = Payment.create(UUID.randomUUID(), 1L, 10_000, PaymentStatus.FAIL, CARD, TOSS);

        // When
        reservationPaymentDomainService.syncReservationWithPayment(reservation, payment);

        // Then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.FAIL);
    }

    @DisplayName("syncReservationWithPayment - Payment의 상태값이 PENDING 일때")
    @Test
    void syncReservationWithPayment_whenPaymentStatusIsPending() {
        // given - PENDING 상태의 Reservation 이 주어졌을 때
        Reservation reservation = Reservation.create(
                1L,
                UUID.randomUUID(),
                UUID.randomUUID(),
                ReservationStatus.PENDING,
                4,
                LocalDate.now(),
                LocalTime.now()
        );
        // and - PENDING 상태의 Payment 가  주어졌을 때
        Payment payment = Payment.create(UUID.randomUUID(), 1L, 10_000, PaymentStatus.PENDING, CARD, TOSS);

        // When
        reservationPaymentDomainService.syncReservationWithPayment(reservation, payment);

        // Then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }
}
