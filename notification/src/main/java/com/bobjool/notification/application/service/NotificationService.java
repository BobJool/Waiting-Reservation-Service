package com.bobjool.notification.application.service;


import com.bobjool.notification.application.dto.NotificationDto;
import com.bobjool.notification.application.dto.NotificationHistoryDto;
import com.bobjool.notification.application.dto.NotificationSearchDto;
import com.bobjool.notification.domain.entity.Notification;
import com.bobjool.notification.domain.entity.Template;
import com.bobjool.notification.domain.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SlackService slackService;
    private final NotificationRepository notificationRepository;
    private final TemplateService templateService;

    public Page<NotificationHistoryDto> searchNotification(NotificationSearchDto searchDto, Pageable pageable) {

        Page<Notification> notificationPage = notificationRepository.search(
                searchDto.userId(),
                searchDto.message(),
                searchDto.date(),
                searchDto.category(),
                searchDto.action(),
                searchDto.channel(),
                pageable
        );

        return notificationPage.map(notification ->
                NotificationHistoryDto.from(notification, notification.getTemplateId())
        );
    }

    @Transactional
    public void postNotification(NotificationDto dto) {
        switch (dto.messageChannel()) {
            case SLACK:
                sendDirectMessage(
                        dto.userSlack(),
                        dto.messageTitle().concat(
                                dto.messageContent())
                );
                break;
            case EMAIL:
            default:
                sendEmail(dto.userEmail(), dto.messageTitle(), dto.messageContent());
        }

    }

    @Transactional
    protected void saveNotification(UUID templateId, Long userId, String jsonData, String message, String userContact) {
        Template template = templateService.selectTemplateEntity(templateId);
        Notification notification = Notification.createNotification(template, userId, jsonData, message, userContact);
        notificationRepository.save(notification);
    }

    private void sendDirectMessage(String slackId, String message) {
        String channelId = slackService.conversationsOpen(slackId);
        slackService.chatPostMessage(channelId, message);
    }

    private void sendEmail(String email, String title, String content) {
        // TODO. Email 라이브러리 적용
    }

}
