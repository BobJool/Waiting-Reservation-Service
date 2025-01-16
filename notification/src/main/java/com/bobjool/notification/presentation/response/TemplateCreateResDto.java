package com.bobjool.notification.presentation.response;

import com.bobjool.notification.application.dto.TemplateDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TemplateCreateResDto(
        UUID templateId,
        LocalDateTime createdAt
) {
    public static TemplateCreateResDto from(TemplateDto templateDto) {
        return new TemplateCreateResDto(
                templateDto.id(),
                templateDto.createdAt()
        );
    }
}
