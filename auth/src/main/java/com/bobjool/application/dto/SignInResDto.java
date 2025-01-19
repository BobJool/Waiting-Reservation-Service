package com.bobjool.application.dto;

public record SignInResDto(
        String accessToken,
        String refreshToken
) {

    public static SignInResDto from(String accessToken, String refreshToken) {
        return new SignInResDto(accessToken, refreshToken);
    }
}