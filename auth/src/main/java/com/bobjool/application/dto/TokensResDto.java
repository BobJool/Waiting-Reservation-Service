package com.bobjool.application.dto;

public record TokensResDto(
        String accessToken,
        String refreshToken
) {
}