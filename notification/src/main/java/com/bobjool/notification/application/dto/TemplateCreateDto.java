package com.bobjool.notification.application.dto;

import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;

public record TemplateCreateDto(
        BobjoolServiceType serviceType,
        NotificationChannel channel,
        NotificationType type,
        String title,
        String template
) {
}
