package com.bobjool.reservation.domain.repository;

import com.bobjool.reservation.domain.entity.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Optional<Payment> findById(UUID id);

    Payment save(Payment payment);

    <S extends Payment> List<S> saveAll(Iterable<S> entities);
}
