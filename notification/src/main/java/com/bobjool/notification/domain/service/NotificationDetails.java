package com.bobjool.notification.domain.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
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
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_NOTIFICATION);
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
            throw new BobJoolException(ErrorCode.MISSING_USER_ID_IN_KAFKA_MESSAGE);
        }
        return Long.valueOf(metaData.get(USER_ID.toCamelCase()));
    }

    public UUID getRestaurantId() {
        if (!metaData.containsKey(RESTAURANT_ID.toCamelCase())) {
            throw new BobJoolException(ErrorCode.MISSING_RESTAURANT_ID_IN_KAFKA_MESSAGE);
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
            throw new BobJoolException(ErrorCode.MISSING_DATE_IN_KAFKA_MESSAGE);
        }

        LocalDate localDate = LocalDate.parse(metaData.get(STRING_DATE));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.KOREAN);

        metaData.put(STRING_DATE, localDate.format(formatter));
        log.info("Date format changed: {}", metaData.get(STRING_DATE));
    }

    public void applyTimeFormat() {
        final String STRING_TIME = "time";

        if (!metaData.containsKey(STRING_TIME)) {
            throw new BobJoolException(ErrorCode.MISSING_TIME_IN_KAFKA_MESSAGE);
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
