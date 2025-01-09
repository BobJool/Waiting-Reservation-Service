package com.bobjool.application.dto;

import com.bobjool.domain.entity.User;

public record UpdateUserResDto (
        String slackEmail,
        String slackId,
        String phoneNumber
) {
    public static UpdateUserResDto from(User user) {
        return new UpdateUserResDto(
                user.getSlackEmail(),
                user.getSlackId(),
                user.getPhoneNumber()
        );
    }
}
