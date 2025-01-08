package com.bobjool.gateway;

import lombok.Getter;

@Getter
public enum ExcludedPaths {
    ROOT("/"),
    SIGN_IN("/api/v1/auths/sign-in"),
    SIGN_UP("/api/v1/auths/sign-up"),
    SWAGGER("/swagger-ui/**");

    private final String path;

    ExcludedPaths(String path) {
        this.path = path;
    }
}
