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
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final TemplateConvertService templateConvertService;
    private final EventService eventService;
    private final TemplateMappingConfig templateMappingConfig;

    @KafkaListener(topics = {
            "queue.registered",
            "queue.delayed",
            "queue.canceled",
            "queue.remind",
            "queue.alerted",
            "queue.rush"
    })
    public void handleQueueEvent(String data,
                                 @Header("kafka_receivedTopic") String topic) {
        Map<String, String> map = this.toStringMap(data);
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = this.getTemplateId(BobjoolServiceType.QUEUE, topic);
        log.info("Received kafka message. topic: {}, templateId: {}", topic, templateId);

        eventService.preProcess(channel, templateId, map);
    }

    @KafkaListener(topics = {
            "reservation.completed",
            "reservation.failed",
            "reservation.refund",
            "reservation.remind"
    })
    public void handleReservationEvent(String data,
                                       @Header("kafka_receivedTopic") String topic) {
        Map<String, String> map = this.toStringMap(data);
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = this.getTemplateId(BobjoolServiceType.RESERVATION, topic);

        this.setTimeFormat(map);
        this.setDateFormat(map);
        log.info("Received kafka message. topic: {}, templateId: {}", topic, templateId);

        eventService.preProcess(channel, templateId, map);
    }

    private Map<String, String> toStringMap(String data) {
        log.info("received data: {}", data);
        data = data.substring(1, data.length() - 1);
        data = data.replace("\\", "");

        Map<String, Object> map = EventSerializer.deserialize(data, Map.class);
        log.info("deserialize data: {}", map);

        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.valueOf(entry.getValue())
                ));
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

    /**
     * 시간 포맷을 적용합니다.
     * 20:45 -> 오후 8시 45분
     */
    private void setTimeFormat(Map<String, String> data) {
        data.put("time",
                templateConvertService.formatLocalTime(
                        data.get("time")
                )
        );
    }

    /**
     * 날짜 포맷을 적용합니다.
     * 2025-01-03 -> 2025년 1월 3일
     */
    private void setDateFormat(Map<String, String> data) {
        data.put("date",
                templateConvertService.formatLocalDate(
                        data.get("date")
                )
        );
    }

}
