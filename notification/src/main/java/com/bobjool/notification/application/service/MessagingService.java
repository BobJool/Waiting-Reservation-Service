package com.bobjool.notification.application.service;

import com.bobjool.notification.application.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingService {
    private final SlackService slackService;
    private final MailService mailService;

    public void postNotification(NotificationDto dto) {
        sendDirectMessage(
                dto.userSlack(),
                dto.messageTitle().concat(
                        dto.messageContent())
        );
    }

    public void sendDirectMessage(String slackId, String message) {
        String channelId = slackService.conversationsOpen(slackId);
        slackService.chatPostMessage(channelId, message);
    }

    public void sendEmail(String email, String title, String content) {
        mailService.sendEmailNotification(email, title, content);
    }

}
