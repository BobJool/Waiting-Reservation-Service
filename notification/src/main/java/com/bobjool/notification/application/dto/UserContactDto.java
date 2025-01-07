package com.bobjool.notification.application.dto;

public record UserContactDto(
        String name,
        String slack,
        String email,
        String number
) {
}
