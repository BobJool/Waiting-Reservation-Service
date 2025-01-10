package com.bobjool.notification.application.dto;

import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;

import java.time.LocalDate;

public record NotificationSearchDto(
        Long userId,
        String message,
        LocalDate date,
        BobjoolServiceType category,
        NotificationType action,
        NotificationChannel channel
) {
}
