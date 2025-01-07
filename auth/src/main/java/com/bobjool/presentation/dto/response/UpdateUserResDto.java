package com.bobjool.presentation.dto.response;

import com.bobjool.domain.entity.User;

public record UpdateUserResDto (
        String slackEmail,
        String slackId,
        String phoneNumber
) {
    public static UpdateUserResDto of(User user) {
        return new UpdateUserResDto(
                user.getSlackEmail(),
                user.getSlackId(),
                user.getPhoneNumber()
        );
    }
}
