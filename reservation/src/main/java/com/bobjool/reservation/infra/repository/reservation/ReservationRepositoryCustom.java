package com.bobjool.reservation.infra.repository.reservation;

import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReservationRepositoryCustom {
    Page<Reservation> search(Long userId,
                             UUID restaurantId,
                             UUID restaurantScheduleId,
                             ReservationStatus status,
                             Pageable pageable);
}
