package com.bobjool.notification.application.service;

import com.bobjool.notification.application.client.RestaurantClient;
import com.bobjool.notification.application.client.UserClient;
import com.bobjool.notification.application.dto.NotificationDto;
import com.bobjool.notification.application.dto.RestaurantContactDto;
import com.bobjool.notification.application.dto.TemplateDto;
import com.bobjool.notification.application.dto.UserContactDto;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.service.TemplateConvertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static com.bobjool.notification.domain.entity.TemplateContactField.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final NotificationService notificationService;
    private final TemplateService templateService;
    private final TemplateConvertService templateConvertService;

    private final UserClient userClient;
    private final RestaurantClient restaurantClient;

    /**
     * 알림 전송에 필요한 데이터를 수집하고 변환하여,
     * 히스토리를 저장하고 알림 발송 요청을 보냅니다.
     * Kafka Listener 가 호출합니다.
     * @param channel 알림 채널
     * @param templateId 알림 메시지 템플릿 ID
     * @param data 템플릿 변수 바인딩 데이터
     */
    @Transactional
    public void preProcess(NotificationChannel channel, UUID templateId, Map<String, String> data) {
        this.replaceRestaurantContact(data);
        this.replaceUserContact(data);
        log.info("Contact information replace completed.");

        Long userId = Long.valueOf(data.get(USER_ID.toSnakeCase()));
        String userSlack = data.get(USER_SLACK.toSnakeCase());
        String userEmail = data.get(USER_EMAIL.toSnakeCase());
        String userContact = channel.equals(NotificationChannel.SLACK) ? userSlack : userEmail;
        log.info("User contact load complete, userContact: {}", userContact);

        TemplateDto template = templateService.selectTemplate(templateId);
        String templateData = templateConvertService.getMapToJsonString(data);
        String messageContent = templateConvertService.templateBinding(template.template(), data);
        String messageTitle = templateConvertService.templateBinding(template.title(), data);
        messageTitle = templateConvertService.setTitleBold(messageTitle);
        log.info("Message setting complete with Template: {}", templateData);

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

    private void replaceRestaurantContact(Map<String, String> data) {
        if (!data.containsKey(RESTAURANT_ID.toSnakeCase())) {
            return;
        }
        RestaurantContactDto restaurantContactDto = restaurantClient.getRestaurantContact(
                UUID.fromString(data.get(RESTAURANT_ID.toSnakeCase()))
        ).data();

        data.put(RESTAURANT_NAME.toSnakeCase(), restaurantContactDto.name());
        data.put(RESTAURANT_ADDRESS.toSnakeCase(), restaurantContactDto.address());
        data.put(RESTAURANT_NUMBER.toSnakeCase(), restaurantContactDto.number());
        data.remove(RESTAURANT_ID.toSnakeCase());
    }

    private void replaceUserContact(Map<String, String> data) {
        if (!data.containsKey(USER_ID.toSnakeCase())) {
            return;
        }
        UserContactDto userContactDto = userClient.getUserContact(
                Long.parseLong(data.get(USER_ID.toSnakeCase()))
        ).data();

        data.put(USER_NAME.toSnakeCase(), userContactDto.name());
        data.put(USER_SLACK.toSnakeCase(), userContactDto.slack());
        data.put(USER_EMAIL.toSnakeCase(), userContactDto.email());
    }
}
