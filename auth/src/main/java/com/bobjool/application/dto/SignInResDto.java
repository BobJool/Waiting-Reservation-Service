package com.bobjool.application.dto;

public record SignInResDto(
        String accessToken
) {

    public static SignInResDto from(String accessToken) {
        return new SignInResDto(accessToken);
    }
}