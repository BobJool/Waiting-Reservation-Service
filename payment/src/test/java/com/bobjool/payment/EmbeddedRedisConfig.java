package com.bobjool.payment;

import redis.embedded.RedisServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = new RedisServer(6379); // 포트 설정
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
