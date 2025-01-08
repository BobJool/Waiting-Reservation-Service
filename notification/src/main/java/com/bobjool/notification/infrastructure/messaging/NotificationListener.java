package com.bobjool.notification.infrastructure.messaging;

import com.bobjool.notification.application.service.EventService;
import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.service.TemplateConvertService;
import com.bobjool.notification.infrastructure.config.template.TemplateMappingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final TemplateConvertService templateConvertService;
    private final EventService eventService;
    private final TemplateMappingConfig templateMappingConfig;

    @KafkaListener(topics = {
            "waiting.registered",
            "waiting.delayed",
            "waiting.canceled",
            "waiting.remind",
            "waiting.alerted",
            "waiting.rush"
    })
    public void handleQueueEvent(Map<String, String> data,
                                 @Header("kafka_receivedTopic") String topic) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = this.getTemplateId(BobjoolServiceType.QUEUE, topic);

        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = {
            "reservation.completed",
            "reservation.failed",
            "reservation.refund",
            "reservation.remind"
    })
    public void handleReservationEvent(Map<String, String> data,
                                       @Header("kafka_receivedTopic") String topic) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = this.getTemplateId(BobjoolServiceType.RESERVATION, topic);

        this.setTimeFormat(data);
        this.setDateFormat(data);

        eventService.preProcess(channel, templateId, data);
    }

    private UUID getTemplateId(BobjoolServiceType category, String topic) {
        String[] depth = topic.split("\\.");

        String action = depth[1];
        String templateId = switch (category) {
            case QUEUE -> templateMappingConfig.getQueue().get(action);
            case RESERVATION -> templateMappingConfig.getReservation().get(action);
        };

        return UUID.fromString(templateId);
    }

    private void setTimeFormat(Map<String, String> data) {
        data.put("time",
                templateConvertService.formatLocalTime(
                        data.get("time")
                )
        );
    }

    private void setDateFormat(Map<String, String> data) {
        data.put("date",
                templateConvertService.formatLocalDate(
                        data.get("date")
                )
        );
    }

}
