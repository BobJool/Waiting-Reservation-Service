package com.bobjool.notification.presentation.controller;

import com.bobjool.notification.application.service.MessagingService;
import com.bobjool.notification.presentation.request.NotificationReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications/message")
@RequiredArgsConstructor
public class MessagingController {
    private final MessagingService messagingService;

    @PostMapping
    public void postNotification(@Valid @RequestBody NotificationReqDto reqDto) {
        messagingService.postNotification(reqDto.toServiceDto());
    }

}
