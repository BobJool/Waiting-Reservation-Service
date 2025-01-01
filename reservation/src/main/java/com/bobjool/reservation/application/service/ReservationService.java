package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.application.dto.reservation.ReservationCreateDto;
import com.bobjool.reservation.application.dto.reservation.ReservationResDto;
import com.bobjool.reservation.application.dto.reservation.ReservationUpdateDto;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import com.bobjool.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationResDto createReservation(ReservationCreateDto reservationCreateDto) {
        log.info("createReservation.ReservationCreateDto = {}", reservationCreateDto);
        // todo restaurant schedule 의 current_capacity를 올려주는 로직 필요
        // 먼저 feignClient 로 호출하는 방식으로 구현해서 부하 테스트 -> 카프카 도입
        Reservation reservation = Reservation.create(
                reservationCreateDto.userId(),
                reservationCreateDto.restaurantId(),
                reservationCreateDto.restaurantScheduleId(),
                reservationCreateDto.guestCount());
        return ReservationResDto.from(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResDto updateReservationStatus(ReservationUpdateDto reservationUpdateDto,
                                                     UUID reservationId) {
        log.info("updateReservationStatus.ReservationUpdateDto = {}", reservationUpdateDto);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        reservation.updateStatus(ReservationStatus.of(reservationUpdateDto.status()));
        return ReservationResDto.from(reservation);
    }
}
