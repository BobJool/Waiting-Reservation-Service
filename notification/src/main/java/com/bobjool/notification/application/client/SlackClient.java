package com.bobjool.notification.application.client;

import java.util.Map;

public interface SlackClient {

    String usersLookupByEmail(Map<String, String> emailRequest);

    String conversationsOpen(Map<String, String> conversationRequest);

    String chatPostMessage(Map<String, String> messageRequest);

}
