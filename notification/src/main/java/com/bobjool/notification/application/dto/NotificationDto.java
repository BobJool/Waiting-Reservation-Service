package com.bobjool.notification.application.dto;

import com.bobjool.notification.domain.service.NotificationDetails;
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
    public static NotificationDto from(
            NotificationDetails details
    ) {
        return new NotificationDto(
                details.getUserId(),
                details.getUserEmail(),
                details.getUserSlack(),
                details.getMessageChannel(),
                details.getMessageTitle(),
                details.getMessageContent(),
                details.getTemplateId(),
                details.getTemplateData()
        );
    }
}
