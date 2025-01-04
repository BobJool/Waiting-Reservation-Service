package com.bobjool.notification.presentation.response;

import com.bobjool.notification.application.dto.TemplateDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TemplateUpdateResDto(
        UUID templateId,
        LocalDateTime updatedAt
) {
    public static TemplateUpdateResDto from(TemplateDto templateDto) {
        return new TemplateUpdateResDto(
                templateDto.id(),
                templateDto.updatedAt()
        );
    }
}
