package com.bobjool.payment.infra.repository;

import com.bobjool.payment.domain.entity.Payment;
import com.bobjool.payment.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * querydsl 을 위한 인터페이스
 * */
public interface PaymentRepositoryCustom {

    Page<Payment> search(Long userId,
                         PaymentStatus status,
                         LocalDate startDate,
                         LocalDate endDate,
                         Pageable pageable);
}
