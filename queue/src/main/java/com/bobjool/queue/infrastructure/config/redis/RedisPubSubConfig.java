package com.bobjool.queue.infrastructure.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.bobjool.queue.application.service.RedisExpirationListener;

@Configuration
public class RedisPubSubConfig {

	@Bean
	public RedisMessageListenerContainer container(
		RedisConnectionFactory connectionFactory,
		RedisExpirationListener expirationListener
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);

		container.addMessageListener(
			createListenerAdapter(expirationListener),
			new PatternTopic("__keyevent@*__:expired") // Redis 키 만료 이벤트 패턴
		);

		return container;
	}

	private MessageListenerAdapter createListenerAdapter(Object listener) {
		return new MessageListenerAdapter(listener, "onMessage");
	}

}
