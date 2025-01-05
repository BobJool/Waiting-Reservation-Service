package com.bobjool.presentation.dto.response;

import com.bobjool.domain.entity.User;
import com.bobjool.domain.entity.UserRole;

public record UserResDto(
        Long id,
        String username,
        String name,
        String nickname,
        String email,
        String slackId,
        String phoneNumber,
        Boolean isApproved,
        UserRole role
) {
    public static UserResDto of(User user) {
        return new UserResDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getSlackId(),
                user.getPhoneNumber(),
                user.getIsApproved(),
                user.getRole()
        );
    }
}
