package com.bobjool.queue.infrastructure.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.bobjool.queue.application.service.QueueMessageSubscriber;

@Configuration
public class RedisPubSubConfig {
	@Bean
	public RedisMessageListenerContainer container(
		RedisConnectionFactory connectionFactory,
		MessageListenerAdapter registerListenerAdapter,
		MessageListenerAdapter delayListenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(registerListenerAdapter, registerTopic());
		container.addMessageListener(delayListenerAdapter, delayTopic());

		return container;
	}

	@Bean
	public MessageListenerAdapter registerListenerAdapter(QueueMessageSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "onMessage");
	}

	@Bean
	public MessageListenerAdapter delayListenerAdapter(QueueMessageSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "onMessage");
	}

	@Bean
	public ChannelTopic registerTopic() {
		return new ChannelTopic("queue.register");
	}

	@Bean
	public ChannelTopic delayTopic() {
		return new ChannelTopic("queue.delay");
	}

}
