package com.bobjool.notification.application.service;

import com.bobjool.notification.application.client.RestaurantClient;
import com.bobjool.notification.application.client.UserClient;
import com.bobjool.notification.application.dto.NotificationDto;
import com.bobjool.notification.application.dto.RestaurantContactDto;
import com.bobjool.notification.application.dto.TemplateDto;
import com.bobjool.notification.application.dto.UserContactDto;
import com.bobjool.notification.domain.service.NotificationDetails;
import com.bobjool.notification.domain.service.TemplateConvertService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "EventService")
@Service
@RequiredArgsConstructor
public class EventService {
    private final HistoryService historyService;
    private final TemplateService templateService;
    private final TemplateConvertService templateConvertService;
    private final MessagingService messagingService;

    private final UserClient userClient;
    private final RestaurantClient restaurantClient;

    public void processNotification(NotificationDetails details) {
        loadContacts(details);
        bindToTemplate(details);
        saveHistory(details);
        pushNotification(details);
    }

    private void loadContacts(NotificationDetails details) {
        try {
            loadRestaurantContact(details);
            loadUserContact(details);
        } catch (FeignException e) {
            // TODO. 예외처리 - Feign 통신 실패 - 연락처 못 불러옴
            log.error("Failed to load contacts from details", e);
        }

        log.info("Contact information replace completed.");
    }

    private void loadRestaurantContact(NotificationDetails details) {
        RestaurantContactDto restaurantContactDto = restaurantClient.getRestaurantContact(
                details.getRestaurantId()
        ).data();
        log.info("Restaurant contact load completed.");
        details.updateRestaurantContact(
                restaurantContactDto.name(),
                restaurantContactDto.address(),
                restaurantContactDto.number()
        );

    }

    private void loadUserContact(NotificationDetails details) {
        UserContactDto userContactDto = userClient.getUserContact(
                details.getUserId()
        ).data();
        log.info("User contact load completed.");

        details.updateUserContact(
                userContactDto.name(),
                userContactDto.slack(),
                userContactDto.email()
        );
    }

    @Transactional(readOnly = true)
    protected void bindToTemplate(NotificationDetails details) {
        TemplateDto template = templateService.selectTemplate(details.getTemplateId());
        String messageContent = templateConvertService.templateBinding(template.template(), details.getMetaData());
        String messageTitle = templateConvertService.templateBinding(template.title(), details.getMetaData());
        details.updateMessage(messageTitle, messageContent);
        details.updateTemplateData(template.variables());
        details.applyMessageTitleStyle();
    }

    private void pushNotification(NotificationDetails details) {
        NotificationDto dto = NotificationDto.from(details);
        messagingService.postNotification(dto);
        log.info("Notification posted successfully");
    }

    @Transactional
    protected void saveHistory(NotificationDetails details) {
        historyService.saveNotification(
                details.getTemplateId(),
                details.getUserId(),
                details.getJsonMetaData(),
                details.getMessageTitle().concat(details.getMessageContent()),
                details.getUserContact()
        );
        log.info("Notification history saved complete");
    }

}
