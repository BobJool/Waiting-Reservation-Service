package com.bobjool.notification.application.dto;

import com.bobjool.notification.domain.entity.NotificationChannel;

import java.util.UUID;

public record NotificationDto(
    Long userId,
    String userEmail,
    String userSlack,
    NotificationChannel messageChannel,
    String messageTitle,
    String messageContent,
    UUID templateId,
    String templateData
) {
}
