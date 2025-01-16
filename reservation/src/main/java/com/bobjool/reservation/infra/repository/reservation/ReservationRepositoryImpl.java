package com.bobjool.reservation.infra.repository.reservation;

import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.repository.ReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationRepositoryImpl
    extends JpaRepository<Reservation, UUID>, ReservationRepository, ReservationRepositoryCustom {
}
