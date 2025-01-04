package com.bobjool.notification.presentation.request;

import com.bobjool.notification.application.dto.TemplateCreateDto;
import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;

public record TemplateReqDto(
        @NotBlank(message = "서비스 타입을 입력해주세요.")
        String serviceType,
        @NotBlank(message = "메시지 채널을 입력해주세요.")
        String channel,
        @NotBlank(message = "알림 종류를 입력해주세요.")
        String type,
        @NotBlank(message = "메시지 제목을 입력해주세요.")
        String title,
        @NotBlank(message = "메시지 내용을 입력해주세요.")
        String template
) {
    public TemplateCreateDto toServiceDto() {
        return new TemplateCreateDto(
                BobjoolServiceType.of(serviceType),
                NotificationChannel.of(channel),
                NotificationType.of(type),
                title,
                template
        );
    }
}
