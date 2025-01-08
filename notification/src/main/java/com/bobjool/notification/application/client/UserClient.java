package com.bobjool.notification.application.client;

import com.bobjool.notification.application.dto.ApiResponseDto;
import com.bobjool.notification.application.dto.UserContactDto;

public interface UserClient {
    ApiResponseDto<UserContactDto> getUserContact(Long userId);
}
