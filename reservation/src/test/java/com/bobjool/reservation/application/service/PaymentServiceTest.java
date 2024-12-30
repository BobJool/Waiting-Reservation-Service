package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.reservation.application.dto.PaymentCreateDto;
import com.bobjool.reservation.application.dto.PaymentResponse;
import com.bobjool.reservation.application.dto.PaymentSearchDto;
import com.bobjool.reservation.application.interfaces.PgClient;
import com.bobjool.reservation.domain.entity.Payment;
import com.bobjool.reservation.domain.enums.PaymentMethod;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import com.bobjool.reservation.domain.enums.PgName;
import com.bobjool.reservation.domain.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    // 얘는 가짜 객체입니다. 마음대로 조종 가능합니다.
    @MockBean
    private PgClient pgClient;

    /**
     * createPayment 테스트 5개
     * */
    @DisplayName("createPayment 는 Payment를 저장하고 PaymentResponse 를 반환한다.")
    @Test
    void createPayment_whenValidInput() {
        // given - 적절한 요청이 들어왔을 때
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservationId, userId, amount, method, pgName);

        // PgClient의 requestPayment 메서드가 항상 true를 반환하도록 설정
        given(pgClient.requestPayment(any(Payment.class))).willReturn(true);

        // when - paymentService.createPayment() 를 호출하면!
        PaymentResponse response = paymentService.createPayment(paymentCreateDto);

        // then - 적절한 응답
        assertThat(response.PaymentId()).isNotNull();
        assertThat(response.reservationId()).isEqualTo(reservationId);
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
        assertThat(payment.getReservationId()).isEqualTo(reservationId);
        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(payment.getPgName()).isEqualTo(PgName.TOSS);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETE);
        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isNotNull();
        assertThat(payment.getIsDeleted()).isFalse();
    }

    @DisplayName("createPayment - 결제 방식이 이상한 값이면 BobJoolException 발생")
    @Test
    void createPayment_whenInvalidMethod() {
        // given - 결제 방식이 INVALID
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        String method = "INVALID";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservationId, userId, amount, method, pgName);

        // when & then - 예외 발생
        assertThatThrownBy(() -> paymentService.createPayment(paymentCreateDto))
                .isInstanceOf(BobJoolException.class)
                .hasMessage("지원하지 않는 결제 방식입니다.");
    }

    @DisplayName("createPayment - PgName 이 이상한 값이면 BobJoolException 발생")
    @Test
    void createPayment_whenInvalidPgName() {
        // given - 결제 방식이 INVALID
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "INVALID";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservationId, userId, amount, method, pgName);

        // when & then - 예외 발생
        assertThatThrownBy(() -> paymentService.createPayment(paymentCreateDto))
                .isInstanceOf(BobJoolException.class)
                .hasMessage("지원하지 않는 PG사 이름입니다.");
    }

    @DisplayName("createPayment - 결제 금액이 이 양수가 아니면 BobJoolException 발생")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void createPayment_whenInvalidAmount(Integer amount) {
        // given - 결제 금액이 음수, 0
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservationId, userId, amount, method, pgName);

        // when & then - 예외 발생
        assertThatThrownBy(() -> paymentService.createPayment(paymentCreateDto))
                .isInstanceOf(BobJoolException.class)
                .hasMessage("결제 금액은 양수여야 합니다.");
    }

    @DisplayName("createPayment - PG사가 서버 에러를 냈을 때는 예외 발생")
    @Test
    void createPayment_whenPgClientError() {
        // given - 적절한 요청이 들어왔지만, PgClient 에서 문제 발생
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(reservationId, userId, amount, method, pgName);

        // PgClient의 requestPayment 메서드가 false를 반환하도록 설정
        given(pgClient.requestPayment(any(Payment.class))).willReturn(false);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(paymentCreateDto))
            .isInstanceOf(BobJoolException.class)
                .hasMessage("알 수 없는 이유로 결제에 실패했습니다.");
    }

    /**
     * search 테스트 - 2개
     * */
    @DisplayName("search - 성공")
    @Test
    void search_success() {
        // given - 3개의 결제
        Long userId = 123L;
        String status = "COMPLETE";
        Payment payment1 = Payment.create(UUID.randomUUID(), userId, 1000, PaymentStatus.COMPLETE, PaymentMethod.CARD, PgName.TOSS);
        Payment payment2 = Payment.create(UUID.randomUUID(), 345L, 2000, PaymentStatus.COMPLETE, PaymentMethod.CASH, PgName.NHN);
        Payment payment3 = Payment.create(UUID.randomUUID(), 456L, 3000, PaymentStatus.PENDING, PaymentMethod.CARD, PgName.TOSS);
        paymentRepository.saveAll(List.of(payment1, payment2, payment3));
        PaymentSearchDto paymentSearchDto = new PaymentSearchDto(userId, status, null, null);
        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<PaymentResponse> result = paymentService.search(paymentSearchDto, pageable);

        // then
        assertThat(result.getContent()).hasSize(1)
                .extracting("userId", "amount", "status", "method", "pgName")
                .contains(
                        tuple(userId, 1000, "COMPLETE", "CARD", "TOSS")
                );
    }

    @DisplayName("search - status가 지원하지 않는 값이면 예외 발생")
    @Test
    void search_whenInvalidStatus() {
        // given - 잘못된 status 값
        Long userId = 12345L;
        String invalidStatus = "INVALID";
        LocalDate startDate = LocalDate.of(2024, 12, 28);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Pageable pageable = Pageable.ofSize(10);

        // 검색 DTO 생성
        PaymentSearchDto searchDto = new PaymentSearchDto(userId, invalidStatus, startDate, endDate);

        // when & then - 예외 발생 확인
        assertThatThrownBy(() -> paymentService.search(searchDto, pageable))
                .isInstanceOf(BobJoolException.class)
                .hasMessage("지원하지 않는 결제 상태입니다.");
    }
}