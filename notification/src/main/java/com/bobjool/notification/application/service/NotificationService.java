package com.bobjool.notification.application.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SlackService slackService;

    private boolean sendDirectMessage(String email, String message) {
        String userId = slackService.usersLookupByEmail(email);
        String channelId = slackService.conversationsOpen(userId);

        return slackService.chatPostMessage(channelId, message);
    }
}
