package com.bobjool.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisService redisService;

    private static final String ACCESS_TOKEN_PREFIX = "blacklist:accessToken:";
    private static final String REFRESH_TOKEN_PREFIX = "blacklist:refreshToken:";

    public void addToBlacklist(String token, long expirationInMillis, boolean isAccessToken) {
        String key = getKey(token, isAccessToken);
        redisService.set(key, "blacklisted", Duration.ofMillis(expirationInMillis));
    }

    public boolean isBlacklisted(String token, boolean isAccessToken) {
        String key = getKey(token, isAccessToken);
        return redisService.hasKey(key);
    }

    private String getKey(String token, boolean isAccessToken) {
        return (isAccessToken ? ACCESS_TOKEN_PREFIX : REFRESH_TOKEN_PREFIX) + token;
    }
}
