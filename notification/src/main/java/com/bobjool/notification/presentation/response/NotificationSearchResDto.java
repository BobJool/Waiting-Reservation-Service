package com.bobjool.notification.presentation.response;

import com.bobjool.notification.application.dto.NotificationHistoryDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationSearchResDto(
        Long userId,
        String category,
        String action,
        String channel,
        String contact,
        String message,
        UUID templateId,
        String templateData,
        LocalDateTime createdAt
) {
    public static NotificationSearchResDto from(NotificationHistoryDto historyDto) {
        return new NotificationSearchResDto(
                historyDto.userId(),
                historyDto.category().name(),
                historyDto.action().name(),
                historyDto.channel().name(),
                historyDto.contact(),
                historyDto.message(),
                historyDto.templateId(),
                historyDto.templateData(),
                historyDto.createdAt()
        );
    }
}
