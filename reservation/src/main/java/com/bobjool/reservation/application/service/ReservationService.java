package com.bobjool.reservation.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.application.client.RestaurantScheduleClient;
import com.bobjool.reservation.application.client.RestaurantScheduleReserveReqDto;
import com.bobjool.reservation.application.dto.reservation.ReservationCreateDto;
import com.bobjool.reservation.application.dto.reservation.ReservationResDto;
import com.bobjool.reservation.application.dto.reservation.ReservationSearchDto;
import com.bobjool.reservation.application.dto.reservation.ReservationUpdateDto;
import com.bobjool.reservation.application.events.*;
import com.bobjool.reservation.application.interfaces.ReservationProducer;
import com.bobjool.reservation.domain.entity.Reservation;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import com.bobjool.reservation.domain.enums.ReservationTopic;
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
    private final ReservationProducer reservationProducer;
    private final RestaurantScheduleClient restaurantScheduleClient;

    @Transactional
    public ReservationResDto createReservation(ReservationCreateDto reservationCreateDto) {
        log.info("createReservation.ReservationCreateDto = {}", reservationCreateDto);
        // restaurantSchedule 예약 api 호출
        restaurantScheduleClient.reserveSchedule2(reservationCreateDto.restaurantScheduleId(),
                new RestaurantScheduleReserveReqDto(reservationCreateDto.userId(), reservationCreateDto.guestCount()));

        // 1. DB 저장
        Reservation reservation = Reservation.create(
                reservationCreateDto.userId(),
                reservationCreateDto.restaurantId(),
                reservationCreateDto.restaurantScheduleId(),
                reservationCreateDto.guestCount(),
                reservationCreateDto.reservationDate(),
                reservationCreateDto.reservationTime());
        reservationRepository.save(reservation);

        // 2. reservation.created 토픽에 이벤트 발행
        ReservationCreatedEvent event = ReservationCreatedEvent.from(reservation);
        reservationProducer.publishReservationCreated(ReservationTopic.RESERVATION_CREATED.getTopic(), event);
        return ReservationResDto.from(reservation);
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
     * 예약 완료 처리 후, reservation.completed 이벤트 발행
     * */
    @Transactional
    public void updateReservationCompleted(PaymentCompletedEvent event) {
        log.info("updateReservationCompleted.PaymentCompletedEvent = {}", event);

        Reservation reservation = reservationRepository.findById(event.reservationId())
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        reservation.updateStatus(ReservationStatus.COMPLETE);

        // reservation.completed 이벤트 발행
        ReservationCompletedEvent reservationCompletedEvent = ReservationCompletedEvent.from(reservation);
        reservationProducer.publishReservationCompleted(ReservationTopic.RESERVATION_COMPLETED.getTopic(), reservationCompletedEvent);
    }

    /**
     * 외부 PG사에 결제 실패에 의한 예약 실패 처리 및, reservation.failed 이벤트 발행
     * */
    @Transactional
    public void updateReservationFailed(PaymentFailedEvent event) {
        log.info("updateReservationFailed.PaymentFailedEvent = {}", event);

        Reservation reservation = reservationRepository.findById(event.reservationId())
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        reservation.updateStatus(ReservationStatus.FAIL);

        // reservation.failed 이벤트 발행
        ReservationFailedEvent reservationFailedEvent = ReservationFailedEvent.from(reservation);
        reservationProducer.publishReservationFailed(ReservationTopic.RESERVATION_FAILED.getTopic(), reservationFailedEvent);
    }

    /**
     * 결제 시간 초과에 의한 예약 실패 처리 및, reservation.failed 이벤트 발행
     * */
    @Transactional
    public void updateReservationFailed(PaymentTimeoutEvent event) {
        log.info("updateReservationFailed.PaymentTimeoutEvent = {}", event);

        Reservation reservation = reservationRepository.findById(event.reservationId())
                .orElseThrow(() -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND));

        reservation.updateStatus(ReservationStatus.FAIL);

        // reservation.failed 이벤트 발행
        ReservationFailedEvent reservationFailedEvent = ReservationFailedEvent.from(reservation);
        reservationProducer.publishReservationFailed(ReservationTopic.RESERVATION_FAILED.getTopic(), reservationFailedEvent);
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
