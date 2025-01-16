package com.bobjool.application.dto;

import com.bobjool.domain.entity.UserRole;

public record SignUpDto (
        String username,
        String password,
        String name,
        String nickname,
        String email,
        String slackEmail,
        String phoneNumber,
        UserRole role
) {
}