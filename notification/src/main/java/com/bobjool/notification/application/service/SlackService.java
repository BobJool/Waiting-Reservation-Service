package com.bobjool.notification.application.service;

import com.bobjool.common.exception.BobJoolException;
import com.bobjool.common.exception.ErrorCode;
import com.bobjool.notification.infrastructure.client.SlackClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackService {
    private final SlackClient slackClient;

    @Value(value = "${token.slack.oauth}")
    private String slackToken;

    /**
     * Slack API를 통해 이메일로 ID를 가져옵니다.
     *
     * @param email Slack Email
     * @return Slack ID
     */
    public String usersLookupByEmail(String email) {
        Map<String, String> emailRequest = new HashMap<>();
        emailRequest.put("token", slackToken);
        emailRequest.put("email", email);

        String response = slackClient.usersLookupByEmail(emailRequest);

        JsonElement json = JsonParser.parseString(response);

        boolean success = json.getAsJsonObject().get("ok").getAsBoolean();

        if (!success) {
            String error = json.getAsJsonObject().get("error").getAsString();
            log.error(error);
            throw new BobJoolException(ErrorCode.INVALID_SLACK_EMAIL);
        }

        JsonElement user = json.getAsJsonObject().get("user");
        return user.getAsJsonObject().get("id").getAsString();
    }

    /**
     * Slack API를 통해 다이렉트 메시지 채널을 개설합니다.
     *
     * @param slackId 사용자의 Slack ID
     * @return Channel ID
     */
    public String conversationsOpen(String slackId) {
        Map<String, String> conversationsRequest = new HashMap<>();
        conversationsRequest.put("token", slackToken);
        conversationsRequest.put("users", slackId);

        String response = slackClient.conversationsOpen(conversationsRequest);
        JsonElement json = JsonParser.parseString(response);

        boolean success = json.getAsJsonObject().get("ok").getAsBoolean();

        if (!success) {
            String error = json.getAsJsonObject().get("error").getAsString();
            log.error(error);
            throw new BobJoolException(ErrorCode.SLACK_MESSAGE_FAIL);
        }

        JsonElement user = json.getAsJsonObject().get("channel");
        return user.getAsJsonObject().get("id").getAsString();
    }

    /**
     * Slack API를 통해 채널에 메시지를 전송합니다.
     *
     * @param channelId Channel ID
     * @return 메시지 전송 여부
     */
    public boolean chatPostMessage(String channelId, String message) {
        Map<String, String> messageRequest = new HashMap<>();
        messageRequest.put("token", slackToken);
        messageRequest.put("channel", channelId);
        messageRequest.put("text", message);

        String response = slackClient.chatPostMessage(messageRequest);
        JsonElement json = JsonParser.parseString(response);

        boolean success = json.getAsJsonObject().get("ok").getAsBoolean();

        if (!success) {
            String error = json.getAsJsonObject().get("error").getAsString();
            log.error(error);
            throw new BobJoolException(ErrorCode.SLACK_MESSAGE_FAIL);
        }

        return true;
    }
}

