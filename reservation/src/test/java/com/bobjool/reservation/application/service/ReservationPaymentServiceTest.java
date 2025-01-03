package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.application.dto.payment.PaymentCreateDto;
import com.bobjool.reservation.application.dto.payment.PaymentResDto;
import com.bobjool.reservation.application.interfaces.PgClient;
import com.bobjool.reservation.domain.entity.Payment;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.PaymentMethod;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import com.bobjool.reservation.domain.enums.PgName;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import com.bobjool.reservation.domain.repository.PaymentRepository;
import com.bobjool.reservation.domain.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ReservationPaymentServiceTest {
    @Autowired ReservationPaymentService reservationPaymentService;
    @Autowired ReservationRepository reservationRepository;
    @Autowired PaymentRepository paymentRepository;
    @MockBean PgClient pgClient;

    @DisplayName("createPayment 는 Payment를 저장하고 PaymentReseponse 를 반환한다.")
    @Test
    void createPayment_whenValidInput() {
        // given - PENDING 상태의 reservation 이 저장되어 있고
        Long userId = 12345L;
        Reservation reservation = Reservation.create(userId, UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.PENDING, 4);
        reservationRepository.save(reservation);

        // and - 적절한 요청이 들어왔을 때
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservation.getId(), userId, amount, method, pgName);

        // and - PgClient의 requestPayment 메서드가 true를 반환하도록 설정
        given(pgClient.requestPayment(any(Payment.class))).willReturn(true);

        // when - paymentService.createPayment() 를 호출하면!
        PaymentResDto response = reservationPaymentService.createPayment(paymentCreateDto);

        // then - 적절한 응답
        assertThat(response.PaymentId()).isNotNull();
        assertThat(response.reservationId()).isEqualTo(reservation.getId());
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.amount()).isEqualTo(amount);
        assertThat(response.method()).isEqualTo(PaymentMethod.CARD.name());
        assertThat(response.pgName()).isEqualTo(PgName.TOSS.name());
        assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETE.name());
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();

        // and - Payment 가 저장되었다.
        Payment payment = paymentRepository.findById(response.PaymentId()).orElse(null);
        assertThat(payment).isNotNull();
        assertThat(payment.getId()).isEqualTo(response.PaymentId());
        assertThat(payment.getReservationId()).isEqualTo(reservation.getId());
        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(payment.getPgName()).isEqualTo(PgName.TOSS);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETE);
        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isNotNull();
        assertThat(payment.getIsDeleted()).isFalse();

        // and - Reservation 의 status 도 COMPLETE
        Reservation updatedReservation = reservationRepository.findById(response.reservationId()).orElse(null);
        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.COMPLETE);
    }

    @DisplayName("createPayment - 결제 방식이 유효하지 않을 때")
    @Test
    void createPayment_whenInvalidMethod() {
        // given - PENDING 상태의 reservation 이 저장되어 있고
        Long userId = 12345L;
        Reservation reservation = Reservation.create(userId, UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.PENDING, 4);
        reservationRepository.save(reservation);

        // and - 결제 방식이 유효하지 않은 값일 때
        Integer amount = 10_000;
        String method = "INVALID";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservation.getId(), userId, amount, method, pgName);

        // and - PgClient의 requestPayment 메서드가 true를 반환하도록 설정
        given(pgClient.requestPayment(any(Payment.class))).willReturn(true);

        // when & then - 예외 발생
        assertThatThrownBy(() -> reservationPaymentService.createPayment(paymentCreateDto))
                .isInstanceOf(BobJoolException.class)
                .hasMessage("지원하지 않는 결제 방식입니다.");
    }

    @DisplayName("createPayment - 유효하지 않은 PG사")
    @Test
    void createPayment_whenInvalidPgName() {
        // given - PENDING 상태의 reservation 이 저장되어 있고
        Long userId = 12345L;
        Reservation reservation = Reservation.create(userId, UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.PENDING, 4);
        reservationRepository.save(reservation);

        // and - PgName이 유효하지 않은 값일 때
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "INVALID";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservation.getId(), userId, amount, method, pgName);

        // and - PgClient의 requestPayment 메서드가 true를 반환하도록 설정
        given(pgClient.requestPayment(any(Payment.class))).willReturn(true);

        // when & then - 예외 발생
        assertThatThrownBy(() -> reservationPaymentService.createPayment(paymentCreateDto))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.UNSUPPORTED_PG_NAME.getMessage());
    }

    @DisplayName("createPayment - 결제 금액이 양수가 아닐 때")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void createPayment_whenNegativeAmount(Integer amount) {
        // given - PENDING 상태의 reservation 이 저장되어 있고
        Long userId = 12345L;
        Reservation reservation = Reservation.create(userId, UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.PENDING, 4);
        reservationRepository.save(reservation);

        // and - PgName이 유효하지 않은 값일 때
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservation.getId(), userId, amount, method, pgName);

        // and - PgClient의 requestPayment 메서드가 true를 반환하도록 설정
        given(pgClient.requestPayment(any(Payment.class))).willReturn(true);

        // when & then - 예외 발생
        assertThatThrownBy(() -> reservationPaymentService.createPayment(paymentCreateDto))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.INVALID_PAYMENT_AMOUNT.getMessage());
    }

    @DisplayName("createPayment - PG사가 서버 에러를 냈을 때")
    @Test
    void createPayment_whenPGServerError() {
        // given - PENDING 상태의 reservation 이 저장되어 있고
        Long userId = 12345L;
        Reservation reservation = Reservation.create(userId, UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.PENDING, 4);
        reservationRepository.save(reservation);

        // and - 적절한 요청이 들어왔을 때
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservation.getId(), userId, amount, method, pgName);

        // and - PgClient의 requestPayment 메서드가 false를 반환하도록 설정
        given(pgClient.requestPayment(any(Payment.class))).willReturn(false);

        // when - paymentService.createPayment() 를 호출하면!
        PaymentResDto response = reservationPaymentService.createPayment(paymentCreateDto);

        // then - 적절한 응답
        assertThat(response.PaymentId()).isNotNull();
        assertThat(response.reservationId()).isEqualTo(reservation.getId());
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.amount()).isEqualTo(amount);
        assertThat(response.method()).isEqualTo(PaymentMethod.CARD.name());
        assertThat(response.pgName()).isEqualTo(PgName.TOSS.name());
        assertThat(response.status()).isEqualTo(PaymentStatus.FAIL.name());
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();

        // and - Payment 가 저장되었다.
        Payment payment = paymentRepository.findById(response.PaymentId()).orElse(null);
        assertThat(payment).isNotNull();
        assertThat(payment.getId()).isEqualTo(response.PaymentId());
        assertThat(payment.getReservationId()).isEqualTo(reservation.getId());
        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(payment.getPgName()).isEqualTo(PgName.TOSS);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAIL);
        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isNotNull();
        assertThat(payment.getIsDeleted()).isFalse();

        // and - Reservation 의 status 도 COMPLETE
        Reservation updatedReservation = reservationRepository.findById(response.reservationId()).orElse(null);
        assertThat(updatedReservation.getStatus()).isEqualTo(ReservationStatus.FAIL);
    }

    @DisplayName("createPayment - Reservation 이 없을 때")
    @Test
    void createPayment_whenNonExistReservation() {
        // given - reservation 이 없는 경우
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservationId, userId, amount, method, pgName);

        // and - PgClient의 requestPayment 메서드가 true를 반환하도록 설정
        given(pgClient.requestPayment(any(Payment.class))).willReturn(true);

        // when & then - 예외 발생
        assertThatThrownBy(() -> reservationPaymentService.createPayment(paymentCreateDto))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }

    @DisplayName("createPayment - Reservation 의 상태가 PENDING 이 아닐 때")
    @Test
    void createPayment_whenReservationNotPending() {
        // given - reservation 의 상태가 PENDIN 이 아닐 때
        Long userId = 12345L;
        ReservationStatus completeStatus = ReservationStatus.COMPLETE;
        Reservation reservation = Reservation.create(userId, UUID.randomUUID(), UUID.randomUUID(), completeStatus, 4);
        reservationRepository.save(reservation);

        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservation.getId(), userId, amount, method, pgName);

        // and - PgClient의 requestPayment 메서드가 true를 반환하도록 설정
        given(pgClient.requestPayment(any(Payment.class))).willReturn(true);

        // when & then - 예외 발생
        assertThatThrownBy(() -> reservationPaymentService.createPayment(paymentCreateDto))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.PAYMENT_FAIL.getMessage());
    }
}