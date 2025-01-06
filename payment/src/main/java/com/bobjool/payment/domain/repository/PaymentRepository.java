package com.bobjool.payment.domain.repository;

import com.bobjool.payment.domain.entity.Payment;
import com.bobjool.payment.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Optional<Payment> findById(UUID id);

    Payment save(Payment payment);

    <S extends Payment> List<S> saveAll(Iterable<S> entities);

    Page<Payment> search(Long userId,
                         PaymentStatus status,
                         LocalDate startDate,
                         LocalDate endDate,
                         Pageable pageable);
}
