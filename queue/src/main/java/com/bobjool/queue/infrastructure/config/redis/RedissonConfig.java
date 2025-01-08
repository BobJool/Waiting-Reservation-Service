package com.bobjool.queue.infrastructure.config.redis;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

	@Bean
	public RedissonClient redissonClient() {
		Config  config = new Config();
		config.useSingleServer()
			.setAddress("redis://localhost:6380")
			.setPassword("password")
			.setConnectionPoolSize(64)
			.setConnectionMinimumIdleSize(10);
		return Redisson.create(config);
	}
}