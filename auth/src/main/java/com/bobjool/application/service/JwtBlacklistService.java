package com.bobjool.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisService redisService;

    private static final String ACCESS_TOKEN_PREFIX = "blacklist:token:access:";
    private static final String REFRESH_TOKEN_PREFIX = "blacklist:token:refresh:";

    public void addToBlacklist(String token, long expirationInMillis, boolean isRefreshToken) {
        String key = getKey(token, isRefreshToken);
        redisService.set(key, "blacklisted", Duration.ofMillis(expirationInMillis));
    }

    public boolean isBlacklisted(String token, boolean isRefreshToken) {
        String key = getKey(token, isRefreshToken);
        return redisService.hasKey(key);
    }

    private String getKey(String token, boolean isRefreshToken) {
        return (isRefreshToken ? REFRESH_TOKEN_PREFIX : ACCESS_TOKEN_PREFIX) + token;
    }
}
