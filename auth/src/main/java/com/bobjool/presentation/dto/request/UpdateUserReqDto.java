package com.bobjool.presentation.dto.request;

import com.bobjool.application.dto.UpdateUserDto;

public record UpdateUserReqDto (
        String currentPassword,
        String newPassword,
        String slackEmail,
        String phoneNumber
) {
    public UpdateUserDto toServiceDto() {
        return new UpdateUserDto(
                currentPassword,
                newPassword,
                slackEmail,
                phoneNumber
        );
    }
}
