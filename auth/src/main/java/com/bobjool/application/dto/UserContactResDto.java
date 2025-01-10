package com.bobjool.application.dto;

import com.bobjool.domain.entity.User;

public record UserContactResDto(
        String name,
        String email,
        String slackId,
        String phoneNumber
) {
    public static UserContactResDto from(User user) {
        return new UserContactResDto(
                user.getName(),
                user.getEmail(),
                user.getSlackId(),
                user.getPhoneNumber()
        );
    }
}
