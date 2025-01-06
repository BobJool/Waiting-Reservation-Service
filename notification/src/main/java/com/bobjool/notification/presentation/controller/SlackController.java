package com.bobjool.notification.presentation.controller;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.common.presentation.SuccessCode;
import com.bobjool.notification.application.service.SlackService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/slack")
@RequiredArgsConstructor
public class SlackController {
    private final SlackService slackService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, String>>> findSlackIdByEmail(@Valid @Email @RequestParam("email") String email) {
        String slackId = slackService.usersLookupByEmail(email);
        return ApiResponse.success(
                SuccessCode.SUCCESS,
                Map.of("slack_id", slackId)
        );
    }
}
