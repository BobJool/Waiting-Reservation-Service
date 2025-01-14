package com.bobjool.notification.domain.service;

import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.bobjool.notification.domain.entity.TemplateContactField.*;

@Slf4j(topic = "NotificationDetails")
@Getter
public class NotificationDetails {
    private String messageTitle;
    private String messageContent;
    private UUID templateId;
    private String templateData;
    private Map<String, String> metaData;
    private BobjoolServiceType serviceType;
    private NotificationChannel messageChannel;
    private NotificationType templateType;

    @Builder
    public NotificationDetails(UUID templateId, Map<String, String> metaData, BobjoolServiceType serviceType, NotificationChannel messageChannel, NotificationType action) {
        this.messageChannel = messageChannel;
        this.serviceType = serviceType;
        this.templateType = action;
        this.templateId = templateId;
        this.metaData = metaData;
    }


    public String getUserContact(){
        switch (messageChannel){
            case SLACK -> {
                return getUserSlack();
            }
            case EMAIL -> {
                return getUserEmail();
            }
            default -> {
                // TODO. 예외처리 - 지원하지 않는 알림 채널입니다.
                throw new IllegalArgumentException("Unsupported channel: " + messageChannel);
            }
        }
    }

    public String getUserSlack() {
        return metaData.get(USER_SLACK.toSnakeCase());
    }

    public String getUserEmail() {
        return metaData.get(USER_EMAIL.toSnakeCase());
    }

    public Long getUserId() {
        if (!metaData.containsKey(USER_ID.toCamelCase())) {
            // TODO. 예외처리 -- USER_ID 미포함
            return null;
        }
        return Long.valueOf(metaData.get(USER_ID.toCamelCase()));
    }

    public UUID getRestaurantId() {
        if (!metaData.containsKey(RESTAURANT_ID.toCamelCase())) {
            // TODO. 예외처리 - 레스토랑 ID 미포함
            return null;
        }
        return UUID.fromString(metaData.get(RESTAURANT_ID.toCamelCase()));
    }
    public void updateTemplateData(String templateData) {
        this.templateData = templateData;
    }
    public void updateUserContact(String name, String slack, String email) {
        metaData.put(USER_NAME.toSnakeCase(), name);
        metaData.put(USER_SLACK.toSnakeCase(), slack);
        metaData.put(USER_EMAIL.toSnakeCase(), email);
    }

    public void updateRestaurantContact(String name, String address, String number) {
        metaData.remove(RESTAURANT_ID.toCamelCase());
        metaData.put(RESTAURANT_NAME.toSnakeCase(), name);
        metaData.put(RESTAURANT_ADDRESS.toSnakeCase(), address);
        metaData.put(RESTAURANT_NUMBER.toSnakeCase(), number);
    }

    public void updateMessage(String title, String content) {
        this.messageTitle = title;
        this.messageContent = content;
    }

    public void applyDateFormat() {
        final String STRING_DATE = "date";
        if (!metaData.containsKey(STRING_DATE)) {
            // TODO. 예외처리 - DATE 미포함
            log.error("date is not saved.");
        }

        LocalDate localDate = LocalDate.parse(metaData.get(STRING_DATE));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN);

        metaData.put(STRING_DATE, localDate.format(formatter));
        log.info("Date format changed: {}", metaData.get(STRING_DATE));
    }

    public void applyTimeFormat() {
        final String STRING_TIME = "time";

        if (!metaData.containsKey(STRING_TIME)) {
            // TODO. 예외처리 TIME 미포함 - 메시지 데이터에서 TIME 정보를 찾을 수 없습니다.
            log.error("time is not saved.");
        }

        LocalTime localTime = LocalTime.parse(metaData.get(STRING_TIME));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh시 mm분", Locale.KOREAN);

        metaData.put(STRING_TIME, localTime.format(formatter));
        log.info("Time format changed: {}", metaData.get(STRING_TIME));
    }

    public void applyMessageTitleStyle() {
        messageTitle = this.addBoldStyle(messageTitle);
        messageTitle = this.addLineBreak(messageTitle);
    }

    private String addBoldStyle(String text) {
        return "*".concat(text).concat("*");
    }

    private String addLineBreak(String text) {
        return text.concat("\n");
    }

    public String getJsonMetaData(){
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        for (Map.Entry<String, String> entry : metaData.entrySet()) {
            jsonBuilder.append("\"");
            jsonBuilder.append(entry.getKey());
            jsonBuilder.append("\": \"");
            jsonBuilder.append(entry.getValue());
            jsonBuilder.append("\",");
        }
        jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

}
