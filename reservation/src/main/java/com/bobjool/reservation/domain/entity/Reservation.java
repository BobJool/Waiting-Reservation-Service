package com.bobjool.reservation.domain.entity;

import com.bobjool.common.domain.entity.BaseEntity;
import com.bobjool.reservation.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_payment")
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

//    @Column(name = "reservation_date", nullable = false)
//    private LocalDateTime reservationDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    public static Reservation create(Long userId, UUID restaurantId, UUID restaurantScheduleId,
                                     ReservationStatus status, Integer guestCount) {
        return Reservation.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .restaurantScheduleId(restaurantScheduleId)
                .status(status)
                .guestCount(guestCount)
                .build();
    }
}
