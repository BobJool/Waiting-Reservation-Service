package com.bobjool.infrastructure.client;

import com.bobjool.application.client.NotificationClient;
import com.bobjool.common.presentation.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "notification-service", path = "/api/v1/slack", dismiss404 = true)
public interface NotificationClientImpl extends NotificationClient {

    @GetMapping("/users")
    ApiResponse<Map<String, String>> findSlackIdByEmail(
            @Valid @Email @RequestParam("email") String email
    );
}