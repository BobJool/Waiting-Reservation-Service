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
    public static TemplateSelectResDto from(TemplateDto createDto) {
        return new TemplateSelectResDto(
                createDto.id(),
                createDto.type().toString(),
                createDto.channel().toString(),
                createDto.type().toString(),
                createDto.title(),
                createDto.template(),
                createDto.variables(),
                createDto.createdAt(),
                createDto.updatedAt(),
                createDto.deletedAt()
        );
    }
}
