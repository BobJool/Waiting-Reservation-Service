package com.bobjool.application.dto;

import com.bobjool.domain.entity.User;

public record UserContactResDto(
        String name,
        String email,
        String slack,
        String number
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
