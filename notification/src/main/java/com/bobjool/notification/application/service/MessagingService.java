package com.bobjool.notification.application.service;

import com.bobjool.notification.application.dto.NotificationDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingService {
    private final SlackService slackService;
    private final MailService mailService;

    @Transactional
    public void postNotification(NotificationDto dto) {
        switch (dto.messageChannel()) {
            case SLACK:
                sendDirectMessage(
                        dto.userSlack(),
                        dto.messageTitle().concat(
                                dto.messageContent())
                );
                break;
            case EMAIL:
            default:
                sendEmail(dto.userEmail(), dto.messageTitle(), dto.messageContent());
        }

    }

    private void sendDirectMessage(String slackId, String message) {
        String channelId = slackService.conversationsOpen(slackId);
        slackService.chatPostMessage(channelId, message);
    }

    public void sendEmail(String email, String title, String content) {
        mailService.sendEmailNotification(email, title, content);
    }

}
