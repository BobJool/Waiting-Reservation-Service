package com.bobjool.notification.presentation.response;

import com.bobjool.notification.application.dto.TemplateDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TemplateSelectResDto(
        UUID id,
        String serviceType,
        String channel,
        String type,
        String title,
        String template,
        String variables,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static TemplateSelectResDto from(TemplateDto templateDto) {
        return new TemplateSelectResDto(
                templateDto.id(),
                templateDto.serviceType().toString(),
                templateDto.channel().toString(),
                templateDto.type().toString(),
                templateDto.title(),
                templateDto.template(),
                templateDto.variables(),
                templateDto.createdAt(),
                templateDto.updatedAt(),
                templateDto.deletedAt()
        );
    }
}
