package com.bobjool.queue.infrastructure.config.redis;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.bobjool.queue.application.service.QueueMessageSubscriber;
import com.bobjool.queue.application.service.RedisExpirationListener;

@Configuration
public class RedisPubSubConfig {

	private static final Map<String, String> TOPICS = Map.of(
		"register", "queue.register",
		"delay", "queue.delay",
		"cancel", "queue.cancel",
		"checkIn", "queue.checkin",
		"alert", "queue.alert",
		"rush", "queue.rush"
	);

	@Bean
	public RedisMessageListenerContainer container(
		RedisConnectionFactory connectionFactory,
		RedisExpirationListener expirationListener,
		QueueMessageSubscriber subscriber
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);

		TOPICS.forEach((name, topic) ->
			container.addMessageListener(createListenerAdapter(subscriber), new ChannelTopic(topic))
		);

		container.addMessageListener(
			createListenerAdapter(expirationListener),
			new PatternTopic("__keyevent@*__:expired") // Redis 키 만료 이벤트 패턴
		);

		return container;
	}

	private MessageListenerAdapter createListenerAdapter(Object listener) {
		return new MessageListenerAdapter(listener, "onMessage");
	}

	@Bean
	public Map<String, ChannelTopic> channelTopics() {
		return TOPICS.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> new ChannelTopic(entry.getValue())
			));
	}

}
