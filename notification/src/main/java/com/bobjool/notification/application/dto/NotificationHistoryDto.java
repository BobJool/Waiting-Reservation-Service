package com.bobjool.notification.application.dto;

import com.bobjool.notification.domain.entity.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationHistoryDto(
        UUID id,
        Long userId,
        BobjoolServiceType category,
        NotificationType action,
        NotificationChannel channel,
        String contact,
        String message,
        UUID templateId,
        String templateData,
        LocalDateTime createdAt
) {
    public static NotificationHistoryDto from(Notification notification, Template template) {
        return new NotificationHistoryDto(
                notification.getId(),
                notification.getUserId(),
                template.getServiceType(),
                template.getType(),
                template.getChannel(),
                notification.getContact(),
                notification.getMessage(),
                template.getId(),
                notification.getJsonData(),
                notification.getCreatedAt()
        );
    }
}
