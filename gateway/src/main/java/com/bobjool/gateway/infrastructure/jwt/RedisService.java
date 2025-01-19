package com.bobjool.gateway.infrastructure.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

	private static final String ACCESS_TOKEN_PREFIX = "blacklist:token:access:";
	private static final String REFRESH_TOKEN_PREFIX = "blacklist:token:refresh:";

	private final RedisTemplate<String, Object> redisTemplate;

	public boolean isBlacklisted(String token, String tokenType) {
		String key = (tokenType.equals("refresh") ? REFRESH_TOKEN_PREFIX : ACCESS_TOKEN_PREFIX) + token;
		return redisTemplate.hasKey(key);
	}
}
