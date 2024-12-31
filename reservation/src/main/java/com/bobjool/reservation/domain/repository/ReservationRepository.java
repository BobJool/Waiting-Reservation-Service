package com.bobjool.reservation.domain.repository;

import com.bobjool.reservation.domain.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> findById(long id);

    Reservation save(Reservation reservation);

    <S extends Reservation> List<S> saveAll(Iterable<S> entities);
}
