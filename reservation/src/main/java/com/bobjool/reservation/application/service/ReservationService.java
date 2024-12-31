package com.bobjool.reservation.application.service;

import com.bobjool.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    /**
     * 예약과 결제가 한 번에 하는 걸로 가정합니다.
     * */
    public void createReservation() {

    }
}
