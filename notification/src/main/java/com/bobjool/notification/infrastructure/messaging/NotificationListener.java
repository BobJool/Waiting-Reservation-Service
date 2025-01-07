package com.bobjool.notification.infrastructure.messaging;

import com.bobjool.notification.application.service.EventService;
import com.bobjool.notification.domain.entity.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @KafkaListener(topics = "queue.registered")
    public void handleRegisteredQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.delayed")
    public void handleDelayedQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.remind")
    public void handleRemindQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.alerted")
    public void handleAlertedQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.rush")
    public void handleRushQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "queue.canceled")
    public void handleCanceledQueue(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "reservation.completed")
    public void handleCompletedReservation(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "reservation.failed")
    public void handleFailedReservation(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "reservation.refund")
    public void handleRefundReservation(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

    @KafkaListener(topics = "reservation.remind")
    public void handleRemindReservation(String message) {
        NotificationChannel channel = NotificationChannel.SLACK;
        UUID templateId = UUID.fromString("");
        Map<String, String> data = new HashMap<>();
        eventService.preProcess(channel, templateId, data);
    }

}
