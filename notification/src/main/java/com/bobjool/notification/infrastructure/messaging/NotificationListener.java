package com.bobjool.notification.infrastructure.messaging;

import com.bobjool.notification.application.service.EventService;
import com.bobjool.notification.domain.entity.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final EventService eventService;

    @Value("${notification.template-mapping.queue.registered}")
    private String queueRegisteredTemplateId;

    @Value("${notification.template-mapping.queue.delayed}")
    private String queueDelayedTemplateId;

    @Value("${notification.template-mapping.queue.canceled}")
    private String queueCanceledTemplateId;

    @Value("${notification.template-mapping.queue.remind}")
    private String queueRemindTemplateId;

    @Value("${notification.template-mapping.queue.alerted}")
    private String queueAlertedTemplateId;

    @Value("${notification.template-mapping.queue.rush}")
    private String queueRushTemplateId;

    @Value("${notification.template-mapping.reservation.completed}")
    private String reservationCompletedTemplateId;

    @Value("${notification.template-mapping.reservation.failed}")
    private String reservationFailedTemplateId;

    @Value("${notification.template-mapping.reservation.refund}")
    private String reservationRefundTemplateId;

    @Value("${notification.template-mapping.reservation.remind}")
    private String reservationRemindTemplateId;

    @KafkaListener(topics = "queue.registered")
    public void handleRegisteredQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(queueRegisteredTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.delayed")
    public void handleDelayedQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(queueDelayedTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.remind")
    public void handleRemindQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(queueRemindTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.alerted")
    public void handleAlertedQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(queueAlertedTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.rush")
    public void handleRushQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(queueRushTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.canceled")
    public void handleCanceledQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(queueCanceledTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "reservation.completed")
    public void handleCompletedReservation(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(reservationCompletedTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "reservation.failed")
    public void handleFailedReservation(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(reservationFailedTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "reservation.refund")
    public void handleRefundReservation(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(reservationRefundTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "reservation.remind")
    public void handleRemindReservation(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString(reservationRemindTemplateId);
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

}
