package com.bobjool.payment.domain.repository;


import com.bobjool.payment.domain.entity.Payment;
import com.bobjool.payment.domain.enums.PaymentMethod;
import com.bobjool.payment.domain.enums.PaymentStatus;
import com.bobjool.payment.domain.enums.PgName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("search - userId를 조건으로 주었을 때")
    @Test
    void search_whenUserId() {
        // given - userId가 조건으로 주어졌을 때
        Long userId = 1L;
        List<Payment> payments = creaetDummyPayments();
        paymentRepository.saveAll(payments);
        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<Payment> result = paymentRepository.search(userId, null, null, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(5)
                .extracting("userId", "amount", "status")
                .containsExactlyInAnyOrder(
                        tuple(userId, 1, PaymentStatus.COMPLETE),
                        tuple(userId, 3, PaymentStatus.COMPLETE),
                        tuple(userId, 5, PaymentStatus.COMPLETE),
                        tuple(userId, 7, PaymentStatus.COMPLETE),
                        tuple(userId, 9, PaymentStatus.COMPLETE)
                );
    }

    @DisplayName("search - status 를 조건으로 주었을 때")
    @Test
    void search_whenStatus() {
        // given - status를 조건으로 주어졌을 때
        PaymentStatus status = PaymentStatus.FAIL;
        List<Payment> payments = creaetDummyPayments();
        paymentRepository.saveAll(payments);
        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<Payment> result = paymentRepository.search(null, status, null, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(5)
                .extracting("userId", "amount", "status")
                .containsExactlyInAnyOrder(
                        tuple(2L, 2, PaymentStatus.FAIL),
                        tuple(2L, 4, PaymentStatus.FAIL),
                        tuple(2L, 6, PaymentStatus.FAIL),
                        tuple(2L, 8, PaymentStatus.FAIL),
                        tuple(2L, 10, PaymentStatus.FAIL)
                );
    }

    @DisplayName("search - pageSize 조건으로 주었을 때")
    @Test
    void search_whenPageSize() {
        // given - pageSize 를  7개로  주어졌을 때
        List<Payment> payments = creaetDummyPayments();
        paymentRepository.saveAll(payments);
        Pageable pageable = PageRequest.of(0, 7);

        // when
        Page<Payment> result = paymentRepository.search(null, null, null, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(7);
    }


    private List<Payment> creaetDummyPayments() {
        List<Payment> payments = new ArrayList<>();
        UUID reservationId = UUID.randomUUID();
        Long userId1 = 1L;
        Long userId2 = 2L;
        PaymentStatus paymentStatus1 = PaymentStatus.COMPLETE;
        PaymentStatus paymentStatus2 = PaymentStatus.FAIL;
        for (int i = 1; i <= 10; i++) {
            Payment payment = null;
            if (i % 2 == 1) {
                payment = Payment.create(reservationId, userId1, i, paymentStatus1, PaymentMethod.CARD, PgName.TOSS);
            } else {
                payment = Payment.create(reservationId, userId2, i, paymentStatus2, PaymentMethod.CASH, PgName.NHN);
            }
            payments.add(payment);
        }
        return payments;
    }
}