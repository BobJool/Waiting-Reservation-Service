package com.bobjool.notification.presentation.request;

import com.bobjool.notification.application.dto.NotificationSearchDto;
import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;

import java.time.LocalDate;

public record NotificationSearchReqDto(
        Long userId,
        String message,
        LocalDate date,
        String category,
        String action,
        String channel
) {
    public static NotificationSearchDto toServiceDto(NotificationSearchReqDto searchDto) {
        return new NotificationSearchDto(
                searchDto.userId(),
                searchDto.message(),
                searchDto.date(),
                searchDto.category() != null ? BobjoolServiceType.of(searchDto.category()) : null,
                searchDto.action() != null ? NotificationType.of(searchDto.action()) : null,
                searchDto.channel() != null ? NotificationChannel.of(searchDto.channel()) : null
        );
    }
}
