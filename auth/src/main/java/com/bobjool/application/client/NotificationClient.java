package com.bobjool.application.client;

import com.bobjool.common.presentation.ApiResponse;

import java.util.Map;

public interface NotificationClient {
    ApiResponse<Map<String, String>> findSlackIdByEmail(
            String email
    );
}