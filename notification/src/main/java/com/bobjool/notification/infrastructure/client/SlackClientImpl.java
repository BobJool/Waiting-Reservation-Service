package com.bobjool.notification.infrastructure.client;

import com.bobjool.notification.application.client.SlackClient;
import com.bobjool.notification.infrastructure.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "slackClient", url = "https://slack.com/api", configuration = FeignConfig.class)
public interface SlackClientImpl extends SlackClient {

    @PostMapping(value = "/users.lookupByEmail", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String usersLookupByEmail(@RequestBody Map<String, String> emailRequest);

    @PostMapping(value = "/conversations.open", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String conversationsOpen(@RequestBody Map<String, String> conversationRequest);

    @PostMapping(value = "/chat.postMessage", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String chatPostMessage(@RequestBody Map<String, String> messageRequest);

}
