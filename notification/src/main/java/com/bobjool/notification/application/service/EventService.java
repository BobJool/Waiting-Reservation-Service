package com.bobjool.notification.application.service;

import com.bobjool.notification.application.dto.NotificationDto;
import com.bobjool.notification.application.dto.TemplateDto;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.service.TemplateConvertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final TemplateService templateService;
    private final NotificationService notificationService;
    private final TemplateConvertService templateConvertService;

    // TODO 1. 리스너 클래스에서 표현 변환 후 호출
    @Transactional
    public void preProcess(NotificationChannel channel, Long userId, UUID templateId, Map<String, String> data) {
        // TODO 2. 유저 정보 가져오기(OpenFeign)
        String userEmail = "";
        String userSlack = "U0879NNBRFB";
        String userContact = channel.equals(NotificationChannel.SLACK) ? userSlack : userEmail;
        log.info("User contact load complete, userEmail: {}, userSlack: {}", userEmail, userSlack);

        TemplateDto template = templateService.selectTemplate(templateId);
        String templateData = templateConvertService.getMapToJsonString(data);
        String messageContent = templateConvertService.templateBinding(template.template(), data);
        String messageTitle = templateConvertService.templateBinding(template.title(), data);
        messageTitle = templateConvertService.setTitleBold(messageTitle);
        log.info("Message data setting complete with Template: {}", templateData);

        NotificationDto dto = new NotificationDto(
                userId,
                userEmail,
                userSlack,
                channel,
                messageTitle,
                messageContent,
                templateId,
                templateData
        );

        notificationService.saveNotification(template.id(), userId, templateData, messageTitle.concat(messageContent), userContact);
        log.info("Notification history saved complete");

        notificationService.postNotification(dto);
        log.info("Notification posted successfully");
    }
}
