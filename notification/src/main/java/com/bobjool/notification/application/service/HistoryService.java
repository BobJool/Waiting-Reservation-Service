package com.bobjool.notification.application.service;


import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.notification.application.dto.NotificationHistoryDto;
import com.bobjool.notification.application.dto.NotificationSearchDto;
import com.bobjool.notification.domain.entity.Notification;
import com.bobjool.notification.domain.entity.NotificationStatus;
import com.bobjool.notification.domain.entity.Template;
import com.bobjool.notification.domain.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j(topic = "HistoryService")
@Service
@RequiredArgsConstructor
public class HistoryService {
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
    public void updateNotificationState(UUID id, NotificationStatus status) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new BobJoolException(ErrorCode.ENTITY_NOT_FOUND)
        );
        notification.updateStatus(status);
    }

    @Transactional
    protected UUID saveNotification(UUID templateId, Long userId, String jsonData, String message, String userContact) {
        Template template = templateService.selectTemplateEntity(templateId);

        Notification notification = Notification.createNotification(template, userId, jsonData, message, userContact);
        notification = notificationRepository.save(notification);
        log.info("Notification history saved complete");

        return notification.getId();
    }

}
