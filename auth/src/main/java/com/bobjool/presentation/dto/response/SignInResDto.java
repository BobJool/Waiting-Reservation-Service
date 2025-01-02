package com.bobjool.presentation.dto.response;

public record SignInResDto(
        String accessToken
) {

    public static SignInResDto of(String accessToken) {
        return new SignInResDto(accessToken);
    }
}