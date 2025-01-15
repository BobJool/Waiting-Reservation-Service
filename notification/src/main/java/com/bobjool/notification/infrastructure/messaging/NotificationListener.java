package com.bobjool.notification.infrastructure.messaging;

import com.bobjool.notification.domain.service.NotificationDetails;
import com.bobjool.notification.application.factory.NotificationDetailsFactory;
import com.bobjool.notification.application.service.EventService;
import com.bobjool.notification.domain.entity.BobjoolServiceType;
import com.bobjool.notification.domain.entity.NotificationChannel;
import com.bobjool.notification.domain.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j(topic = "NotificationListener")
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final EventService eventService;
    private final NotificationDetailsFactory notificationDetailsFactory;

    @KafkaListener(topics = {
            "queue.registered",
            "queue.delayed",
            "queue.canceled",
            "queue.remind",
            "queue.alerted",
            "queue.rush"
    })
    public void handleQueueEvent(String message,
                                 @Header("kafka_receivedTopic") String topic) {
        NotificationDetails details = getNotificationDetails(
                NotificationChannel.SLACK, message, topic
        );
        eventService.processNotification(details);
    }

    @KafkaListener(topics = {
            "reservation.completed",
            "reservation.failed",
            "reservation.refund",
            "reservation.remind"
    })
    public void handleReservationEvent(
            String message,
            @Header("kafka_receivedTopic") String topic
    ) {
        NotificationDetails details = getNotificationDetails(
                NotificationChannel.SLACK, message, topic
        );
        details.applyDateFormat();
        details.applyTimeFormat();

        eventService.processNotification(details);
    }

    private NotificationDetails getNotificationDetails(NotificationChannel channel, String message, String topic) {
        log.debug("Get kafka message. topic: {}, message: {}", topic, message);

        NotificationDetails details = notificationDetailsFactory.builder()
                .channel(channel)
                .serviceType(getNotificationServiceType(topic))
                .action(getNotificationAction(topic))
                .kafkaReceivedMessage(message)
                .build();
        log.info("NotificationDetails build success. service: {}, action: {}, message: {}", details.getMessageChannel(), details.getTemplateType(), details.getMetaData());
        return details;
    }

    private BobjoolServiceType getNotificationServiceType(String topic) {
        String[] depth = topic.split("\\.");
        return BobjoolServiceType.of(depth[0]);
    }

    private NotificationType getNotificationAction(String topic) {
        String[] depth = topic.split("\\.");
        return NotificationType.of(depth[1]);
    }

}
