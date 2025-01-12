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
     *
     * @param channel    알림 채널
     * @param templateId 알림 메시지 템플릿 ID
     * @param data       템플릿 변수 바인딩 데이터
     */
    @Transactional
    public void preProcess(NotificationChannel channel, UUID templateId, Map<String, String> data) {
//        TODO.사용자와 레스토랑 서버가 통신이 불가능할 경우 Dummy 메소드로 호출해 테스트 해주세요.
//          this.replaceRestaurantDummyContact(data);
//          this.replaceUserDummyContact(data);
        this.replaceRestaurantContact(data);
        this.replaceUserContact(data);
        log.info("Contact information replace completed.");

        Long userId = Long.valueOf(data.get(USER_ID.toSnakeCase()));
//      TODO. SlackID를 직접 입력하여 테스트하는 경우 직접 초기화해주세요.
//            String userSlack = "U0Q1W2E3R4";
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

        // TODO. Slack ID를 가져올 수 없을 경우 주석처리하고 테스트 해주세요.
        notificationService.postNotification(dto);
        log.info("Notification posted successfully");
    }

    private void replaceRestaurantDummyContact(Map<String, String> data) {
        data.put(RESTAURANT_NAME.toSnakeCase(), "스파르타 식당");
        data.put(RESTAURANT_ADDRESS.toSnakeCase(), "서울특별시 강남구 테헤란로44길 8 12층");
        data.put(RESTAURANT_NUMBER.toSnakeCase(), "02-123-1234");
    }

    private void replaceRestaurantContact(Map<String, String> data) {
        if (!data.containsKey(RESTAURANT_ID.toCamelCase())) {
            return;
        }
        RestaurantContactDto restaurantContactDto = restaurantClient.getRestaurantContact(
                UUID.fromString(data.get(RESTAURANT_ID.toCamelCase()))
        ).data();

        data.remove(RESTAURANT_ID.toCamelCase());
        data.put(RESTAURANT_NAME.toSnakeCase(), restaurantContactDto.name());
        data.put(RESTAURANT_ADDRESS.toSnakeCase(), restaurantContactDto.address());
        data.put(RESTAURANT_NUMBER.toSnakeCase(), restaurantContactDto.number());
    }

    private void replaceUserDummyContact(Map<String, String> data) {
        data.put(USER_ID.toSnakeCase(), "-1");
        data.put(USER_NAME.toSnakeCase(), "홍길동");
        data.put(USER_SLACK.toSnakeCase(), "U0Q1W2E3R4");
        data.put(USER_EMAIL.toSnakeCase(), "rtan2@sparta.com");
    }

    private void replaceUserContact(Map<String, String> data) {
        if (!data.containsKey(USER_ID.toCamelCase())) {
            return;
        }
        UserContactDto userContactDto = userClient.getUserContact(
                Long.parseLong(data.get(USER_ID.toCamelCase()))
        ).data();

        data.put(USER_ID.toSnakeCase(), data.remove(USER_ID.toCamelCase()));
        data.put(USER_NAME.toSnakeCase(), userContactDto.name());
        data.put(USER_SLACK.toSnakeCase(), userContactDto.slack());
        data.put(USER_EMAIL.toSnakeCase(), userContactDto.email());
    }
}
