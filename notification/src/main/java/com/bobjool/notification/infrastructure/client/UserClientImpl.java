package com.bobjool.notification.infrastructure.client;

import com.bobjool.notification.application.client.UserClient;
import com.bobjool.notification.application.dto.ApiResponseDto;
import com.bobjool.notification.application.dto.UserContactDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface UserClientImpl extends UserClient {
    @GetMapping("/api/v1/users/{userId}/contact")
    ApiResponseDto<UserContactDto> getUserContact(@PathVariable(name = "userId") Long userId);
}
