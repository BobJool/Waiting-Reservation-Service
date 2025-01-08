package com.bobjool.application.dto;

public record UpdateUserDto(
        String currentPassword,
        String newPassword,
        String slackEmail,
        String phoneNumber
) {
}