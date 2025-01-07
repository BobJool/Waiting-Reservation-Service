package com.bobjool.notification.domain.entity;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateContactField {
    USER_ID("사용자 ID"),
    USER_NAME("사용자 이름"),
    USER_EMAIL("사용자 이메일"),
    USER_SLACK("사용자 Slack ID"),
    USER_NUMBER("사용자 전화번호"),
    RESTAURANT_ID("레스토랑 ID"),
    RESTAURANT_NAME("레스토랑 이름"),
    RESTAURANT_ADDRESS("레스토랑 주소"),
    RESTAURANT_NUMBER("레스토랑 전화번호");

    private final String description;

    public static TemplateContactField of(String request) {
        return switch (request.toUpperCase()) {
            case "USER_ID" -> USER_ID;
            case "USER_NAME" -> USER_NAME;
            case "USER_EMAIL" -> USER_EMAIL;
            case "USER_SLACK" -> USER_SLACK;
            case "USER_NUMBER" -> USER_NUMBER;
            case "RESTAURANT_ID" -> RESTAURANT_ID;
            case "RESTAURANT_NAME" -> RESTAURANT_NAME;
            case "RESTAURANT_ADDRESS" -> RESTAURANT_ADDRESS;
            case "RESTAURANT_NUMBER" -> RESTAURANT_NUMBER;
            default -> throw new BobJoolException(ErrorCode.UNSUPPORTED_NOTIFICATION_FIELD);
        };

    }
    public String toSnakeCase() {
        return this.name().toLowerCase();
    }

    public String toCamelCase() {
        String[] parts = this.name().toLowerCase().split("_");
        StringBuilder camelCase = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
                camelCase.append(parts[i].substring(0, 1).toUpperCase())
                        .append(parts[i].substring(1));
        }
        return camelCase.toString();
    }

}
