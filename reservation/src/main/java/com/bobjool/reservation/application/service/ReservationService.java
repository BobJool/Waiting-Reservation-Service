package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.application.dto.reservation.ReservationCreateDto;
import com.bobjool.reservation.application.dto.reservation.ReservationResDto;
import com.bobjool.reservation.application.dto.reservation.ReservationSearchDto;
import com.bobjool.reservation.application.dto.reservation.ReservationUpdateDto;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import com.bobjool.reservation.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * CUSTOMER 는 CHECK_IN, NO_SHOW 상태에서 취소할 수 없고,
     * OWNER 는 모든 상태에서 취소 가능
     * 인증 로직이 완료되면, 파라미터에 role을 추가해서 각각 분기처리
     * */
    @Transactional
    public ReservationResDto cancelReservation(UUID reservationId) {
        log.info("cancelReservation.reservationId = {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        // todo CUSTOMER 의 경우 cancel() 호출, OWNER 의 경우 cancelForOwner() 호출
        reservation.cancel();
        return ReservationResDto.from(reservation);
    }

    public ReservationResDto getReservation(UUID reservationId) {
        log.info("getReservation.reservationId = {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        return ReservationResDto.from(reservation);
    }

    public Page<ReservationResDto> search(ReservationSearchDto searchDto, Pageable pageable) {
        log.info("searchReservation.searchDto = {}, pageable = {}", searchDto, pageable);

        ReservationStatus status = null;
        if (searchDto.status() != null) {
            status = ReservationStatus.of(searchDto.status());
        }

        Page<Reservation> reservationPage = reservationRepository.search(
                searchDto.userId(),
                searchDto.restaurantId(),
                searchDto.restaurantScheduleId(),
                status,
                pageable);
        return reservationPage.map(ReservationResDto::from);
    }
}
