package com.bobjool.reservation.application.client;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RestaurantScheduleResDto(
        UUID RestaurantScheduleId,
        Long userId,
        UUID restaurantId,
        int tableNumber,
        LocalDate date,
        LocalTime timeSlot,
        int maxCapacity,
        int currentCapacity,
        boolean available

) {}
