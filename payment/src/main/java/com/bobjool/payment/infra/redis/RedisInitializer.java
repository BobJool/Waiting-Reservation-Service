package com.bobjool.payment.infra.redis;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisInitializer(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try (var connection = redisConnectionFactory.getConnection()) {
            connection.setConfig("notify-keyspace-events", "Ex"); // Ex: 만료 이벤트 활성화
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure Redis keyspace notifications", e);
        }
    }
}
