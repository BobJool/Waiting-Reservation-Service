package com.bobjool.payment.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.payment.application.dto.PaymentResDto;
import com.bobjool.payment.application.dto.PaymentSearchDto;
import com.bobjool.payment.application.dto.PaymentUpdateDto;
import com.bobjool.payment.domain.entity.Payment;
import com.bobjool.payment.domain.enums.PaymentMethod;
import com.bobjool.payment.domain.enums.PaymentStatus;
import com.bobjool.payment.domain.enums.PgName;
import com.bobjool.payment.domain.repository.PaymentRepository;
import jakarta.persistence.EntityManager;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EntityManager em;

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
        Page<PaymentResDto> result = paymentService.search(paymentSearchDto, pageable);

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

    /**
     * updatePaymentStatus 테스트 2개
     * */
    @DisplayName("updatePaymentStatus - 결제 상태를 업데이트한다.")
    @Test
    void updatePaymentStatus_success() {
        // given - Payment 엔티티가 저장 되어 있을 때
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        PaymentStatus completeStatus = PaymentStatus.COMPLETE;
        PaymentMethod method = PaymentMethod.CARD;
        PgName pgName = PgName.TOSS;
        Payment payment = Payment.create(reservationId, userId, amount, completeStatus, method, pgName);
        paymentRepository.save(payment);

        // and - 업데이트할 상태와 DTO 생성
        String newStatus = "REFUND";
        PaymentUpdateDto updateDto = new PaymentUpdateDto(newStatus);

        // when - 결제 상태 업데이트 호출
        PaymentResDto response = paymentService.updatePaymentStatus(updateDto, payment.getId());

        // then - 응답 및 데이터베이스 상태 검증
        assertThat(response.status()).isEqualTo(newStatus);
        assertThat(response.PaymentId()).isEqualTo(payment.getId());

        // and - 데이터베이스에서 업데이트된 Payment 확인
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElse(null);
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.of(newStatus));
    }

    @DisplayName("updatePaymentStatus - 지원하지 않는 결제 상태이면 예외 발생한다.")
    @Test
    void updatePaymentStatus_whenInvalidStatus() {
        // given - Payment 엔티티가 저장 되어 있을 때
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        PaymentStatus completeStatus = PaymentStatus.COMPLETE;
        PaymentMethod method = PaymentMethod.CARD;
        PgName pgName = PgName.TOSS;
        Payment payment = Payment.create(reservationId, userId, amount, completeStatus, method, pgName);
        paymentRepository.save(payment);

        // and - 업데이트할 상태가 이상하면
        String newStatus = "INVALID";
        PaymentUpdateDto updateDto = new PaymentUpdateDto(newStatus);

        // when & then
        assertThatThrownBy(() -> paymentService.updatePaymentStatus(updateDto, payment.getId()))
                .isInstanceOf(BobJoolException.class)
                .hasMessage("지원하지 않는 결제 상태입니다.");
    }

    @DisplayName("refundPayment - 성공적으로 REFUND 상태로 변경된다")
    @Test
    void refundPayment_success() {
        // given - 상태가 COMPLETE인 Payment 엔티티가 존재할 때
        Payment payment = Payment.create(
                UUID.randomUUID(),
                12345L,
                1000,
                PaymentStatus.COMPLETE,
                PaymentMethod.CARD,
                PgName.TOSS
        );
        paymentRepository.save(payment);

        // when - refundPayment 메서드 호출
        PaymentResDto response = paymentService.refundPayment(payment.getId());

        // then - 상태가 REFUND로 변경되었는지 확인
        assertThat(response.status()).isEqualTo(PaymentStatus.REFUND.name());
    }

    @DisplayName("refundPayment - Payment 엔티티를 찾을 수 없을 때 예외 발생")
    @Test
    void refundPayment_notFound() {
        // given - 존재하지 않는 paymentId
        UUID paymentId = UUID.randomUUID();

        // when & then - ENTITY_NOT_FOUND 예외 발생 확인
        assertThatThrownBy(() -> paymentService.refundPayment(paymentId))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }

    @DisplayName("refundPayment - REFUND 불가능한 상태일 때 예외 발생")
    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "FAIL", "REFUND"})
    void refundPayment_invalidStatus(String statusStr) {
        // given - 상태가 PENDING인 Payment 엔티티가 존재할 때
        Payment payment = Payment.create(
                UUID.randomUUID(),
                12345L,
                1000,
                PaymentStatus.of(statusStr),
                PaymentMethod.CARD,
                PgName.TOSS
        );
        paymentRepository.save(payment);

        // when & then - CANNOT_REFUND 예외 발생 확인
        assertThatThrownBy(() -> paymentService.refundPayment(payment.getId()))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.CANNOT_REFUND.getMessage());
    }

    @DisplayName("getProduct - paymentId 로 Payment 를 조회한다.")
    @Test
    void getProduct_success() {
        // given - Payment 엔티티가 저장되어 있을 때
        Payment payment = Payment.create(
                UUID.randomUUID(),
                12345L,
                1000,
                PaymentStatus.COMPLETE,
                PaymentMethod.CARD,
                PgName.TOSS);
        paymentRepository.save(payment);

        // and - Persistence Context 의 1차 캐시에 남아있을 거니 em.flush, clear 호출
        em.flush();
        em.clear();

        // when
        PaymentResDto response = paymentService.getPayment(payment.getId());
        assertThat(response.PaymentId()).isEqualTo(payment.getId());
        assertThat(response.reservationId()).isEqualTo(payment.getReservationId());
        assertThat(response.userId()).isEqualTo(payment.getUserId());
        assertThat(response.amount()).isEqualTo(payment.getAmount());
        assertThat(response.status()).isEqualTo(PaymentStatus.COMPLETE.name());
    }

    @DisplayName("getProduct - 존재하지 않는 id로 조회하는 경우 예외 발생")
    @Test
    void getProduct_whenNonExistId() {
        // given - 존재하지 않는 ID
        UUID paymentId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> paymentService.getPayment(paymentId))
                .isInstanceOf(BobJoolException.class)
                .hasMessage(ErrorCode.ENTITY_NOT_FOUND.getMessage());
    }
}