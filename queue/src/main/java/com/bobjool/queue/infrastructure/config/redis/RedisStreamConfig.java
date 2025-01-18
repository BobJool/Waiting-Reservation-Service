package com.bobjool.queue.infrastructure.config.redis;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

@Configuration
public class RedisStreamConfig {
	private static final String STREAM_KEY = "queue-stream";

	@Bean
	public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
		RedisConnectionFactory connectionFactory,
		StreamListener<String, MapRecord<String, String, String>> streamListener) {

		// StreamMessageListenerContainer 옵션 설정
		StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions =
			StreamMessageListenerContainer.StreamMessageListenerContainerOptions
				.builder()
				.pollTimeout(Duration.ofMillis(100))
				.build();

		// StreamMessageListenerContainer 생성
		StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
			StreamMessageListenerContainer.create(connectionFactory, containerOptions);

		// Stream 구독 설정
		Subscription subscription = container.receive(
			StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()),
			streamListener
		);

		// 컨테이너 시작
		container.start();

		return container;
	}
}
