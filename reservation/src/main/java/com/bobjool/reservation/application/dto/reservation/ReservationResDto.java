package com.bobjool.reservation.application.dto.reservation;

import com.bobjool.reservation.domain.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationResDto(
        UUID reservationId,
        Long userId,
        UUID restaurantId,
        UUID restaurantScheduleId,
        String status,
        Integer guestCount,
        LocalDate reservationDate,
        LocalTime reservationTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReservationResDto from(Reservation reservation) {
        return new ReservationResDto(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRestaurantId(),
                reservation.getRestaurantScheduleId(),
                reservation.getStatus().name(),
                reservation.getGuestCount(),
                reservation.getReservationDate(),
                reservation.getReservationTime(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
