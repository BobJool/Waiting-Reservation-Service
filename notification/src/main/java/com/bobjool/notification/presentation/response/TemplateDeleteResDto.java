package com.bobjool.notification.presentation.response;

import com.bobjool.notification.application.dto.TemplateDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TemplateDeleteResDto(
        UUID templateId,
        LocalDateTime deletedAt
) {
    public static TemplateDeleteResDto from(
            TemplateDto templateDto
    ){
       return new TemplateDeleteResDto(
               templateDto.id(),
               templateDto.deletedAt()
       );
    }
}
