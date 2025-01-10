package com.bobjool.application.dto;

import com.bobjool.domain.entity.User;
import com.bobjool.domain.entity.UserRole;

import java.time.LocalDateTime;

public record UserResDto(
        Long id,
        String username,
        String name,
        String nickname,
        String email,
        String slackId,
        String phoneNumber,
        Boolean isApproved,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResDto from(User user) {
        return new UserResDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getSlackId(),
                user.getPhoneNumber(),
                user.getIsApproved(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}