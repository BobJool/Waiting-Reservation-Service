package com.bobjool.reservation.domain.repository;

import com.bobjool.reservation.domain.entity.Payment;
import com.bobjool.reservation.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Optional<Payment> findById(UUID id);

    Payment save(Payment payment);

    // todo JpaRepository 에 있는 시그니쳐와 정확하게 동일해야 합니다.
    <S extends Payment> List<S> saveAll(Iterable<S> entities);

    // todo 직접 만든 메서드는 테스트 해봐야 합니다.
    Page<Payment> search(Long userId,
                         PaymentStatus status,
                         LocalDate startDate,
                         LocalDate endDate,
                         Pageable pageable);
}
