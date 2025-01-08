package com.bobjool.notification.presentation.request;

import com.bobjool.notification.application.dto.NotificationDto;
import com.bobjool.notification.domain.entity.NotificationChannel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NotificationReqDto(
        @NotNull(message = "유저 ID를 입력해주세요.")
        Long userId,
        @Email(message = "이메일을 입력해주세요.")
        String userEmail,
        @NotBlank(message = "슬랙 ID를 입력해주세요.")
        String userSlack,
        @NotBlank(message = "알림 채널을 입력해주세요.")
        String messageChannel,
        @NotBlank(message = "알림 제목을 입력해주세요.")
        String messageTitle,
        @NotBlank(message = "알림 본문을 입력해주세요.")
        String messageContent,
        @NotNull(message = "템플릿 ID를 입력해주세요.")
        UUID templateId,
        @NotBlank(message = "템플릿의 데이터를 입력해주세요.")
        String templateData
) {
    public NotificationDto toServiceDto(){
        return new NotificationDto(
                userId,
                userEmail,
                userSlack,
                NotificationChannel.of(messageChannel),
                messageTitle,
                messageContent,
                templateId,
                templateData
        );
    }
}
