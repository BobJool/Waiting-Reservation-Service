package com.bobjool.reservation.domain.entity;

import com.bobjool.common.domain.entity.BaseEntity;
import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_reservation")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "restaurant_schedule_id", nullable = false)
    private UUID restaurantScheduleId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;  // 예약 날짜

    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;  // 예약 시간

    public static Reservation create(Long userId, UUID restaurantId, UUID restaurantScheduleId,
                                     ReservationStatus status, Integer guestCount,
                                     LocalDate reservationDate, LocalTime reservationTime) {
        Reservation reservation = Reservation.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .restaurantScheduleId(restaurantScheduleId)
                .status(status)
                .guestCount(guestCount)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .build();
        reservation.validateGuestCount();
        return reservation;

    }

    public static Reservation create(Long userId, UUID restaurantId, UUID restaurantScheduleId,
                                     Integer guestCount,
                                     LocalDate reservationDate, LocalTime reservationTime) {
        return create(userId, restaurantId, restaurantScheduleId, ReservationStatus.PENDING, guestCount,
                reservationDate, reservationTime);
    }

    private void validateGuestCount() {
        if (guestCount == null ||guestCount <= 0) throw new BobJoolException(ErrorCode.INVALID_GUEST_COUNT);
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }

    /**
     * CUSTOMER 용
     * 도메인 규칙: CHECK_IN, NO_SHOW 상태에서는 취소가 안됩니다.
     * 참고) enum 은 == 으로 비교합니다.
     * */
    public void cancel() {
        if (this.status.canNotCancel()) {
            throw new BobJoolException(ErrorCode.CANNOT_CANCEL);
        }
        updateStatus(ReservationStatus.CANCEL);
    }

    /**
     * OWNER 용
     * 모든 상태에서 취소 가능합니다.
     * */
    public void cancelForOwner() {
        updateStatus(ReservationStatus.CANCEL);
    }

    public boolean isPending() {
        return this.getStatus().isPending();
    }

    public boolean isNotPending() {
        return this.getStatus().isNotPending();
    }
}
