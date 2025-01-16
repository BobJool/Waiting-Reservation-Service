package com.bobjool.notification.application.dto;

import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;
import com.bobjool.notification.domain.entity.Template;

import java.time.LocalDateTime;
import java.util.UUID;

public record TemplateDto(
        UUID id,
        BobjoolServiceType serviceType,
        NotificationChannel channel,
        NotificationType type,
        String title,
        String template,
        String variables,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static TemplateDto from(Template template) {
        return new TemplateDto(
                template.getId(),
                template.getServiceType(),
                template.getChannel(),
                template.getType(),
                template.getTitle(),
                template.getTemplate(),
                template.getVariables(),
                template.getCreatedAt(),
                template.getUpdatedAt(),
                template.getDeletedAt()
        );
    }
}
