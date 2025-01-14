package com.bobjool.notification.application.factory;

import com.bobjool.notification.domain.service.NotificationDetails;
import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;
import com.bobjool.notification.infrastructure.config.template.TemplateMappingConfig;
import com.bobjool.notification.infrastructure.messaging.EventSerializer;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationDetailsFactory {
    private final TemplateMappingConfig templateMappingConfig;

    @Builder
    public NotificationDetails build(NotificationChannel channel, BobjoolServiceType serviceType, NotificationType action, String kafkaReceivedMessage) {
        return NotificationDetails.builder()
                .messageChannel(channel)
                .serviceType(serviceType)
                .action(action)
                .templateId(getTemplateId(serviceType, action))
                .metaData(convertKafkaMessageToMap(kafkaReceivedMessage))
                .build();
    }

    private UUID getTemplateId(BobjoolServiceType serviceType, NotificationType action) {
        String templateId = switch (serviceType) {
            case QUEUE -> templateMappingConfig.getQueue().get(action.name().toLowerCase());
            case RESERVATION -> templateMappingConfig.getReservation().get(action.name().toLowerCase());
        };
        if (templateId == null || templateId.isEmpty()) {
            // TODO. 에러처리 - 템플릿 ID 비어있다.
            throw new IllegalArgumentException("Template ID is missing for serviceType: " + serviceType + ", action: " + action);
        }
        return UUID.fromString(templateId);
    }

    private Map<String, String> convertKafkaMessageToMap(String kafkaMessage) {
        String jsonMessage = transformKafkaMessageJsonFormat(kafkaMessage);
        return DeserializeKafkaMessage(jsonMessage);
    }

    private String transformKafkaMessageJsonFormat(String data) {
        return data
                .substring(1, data.length() - 1)
                .replace("\\", "");
    }

    private Map<String, String> DeserializeKafkaMessage(String kafkaReceivedMessage) {
        Map<String, Object> map = EventSerializer.deserialize(kafkaReceivedMessage, Map.class);
        return map
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.valueOf(entry.getValue())
                ));

    }
}
