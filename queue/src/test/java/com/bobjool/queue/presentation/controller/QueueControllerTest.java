package com.bobjool.queue.presentation.controller;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;

import com.bobjool.queue.application.dto.redis.QueueRegisterDto;
import com.bobjool.queue.application.service.QueueService;
import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueType;
import com.bobjool.queue.domain.util.RedisKeyUtil;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueueControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(QueueControllerTest.class);

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private QueueService queueService;

	@BeforeEach
	void setUp() {// 모든 키 삭제
		Set<String> keys = redisTemplate.keys("*");
		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}

	@DisplayName("registerQueue - 동시에 100명이 대기열에 등록")
	@Test
	void registerQueue() throws InterruptedException {
		// given
		UUID restaurantId = UUID.randomUUID();
		int member = 4;
		QueueType type = QueueType.ONLINE;
		DiningOption option = DiningOption.IN_STORE;
		int userCount = 100;

		ExecutorService executorService = Executors.newFixedThreadPool(userCount);
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)executorService;
		CountDownLatch countDownLatch = new CountDownLatch(userCount);

		// when
		for (int i = 1; i <= userCount; i++) {
			final Long userId = (long)i;
			executorService.submit(() -> {
				try {
					QueueRegisterDto request = new QueueRegisterDto(
						restaurantId,
						userId,
						member,
						type,
						option
					);
					queueService.registerQueue(request);
				} catch (Exception e) {
					e.printStackTrace(); // 예외 출력
				} finally {
					countDownLatch.countDown();
				}
			});
			logger.info("excutorService active" + ((ThreadPoolExecutor)executorService).getActiveCount());
		}

		countDownLatch.await();
		executorService.shutdown();

		Thread.sleep(1000); // 데이터 저장 대기

		verifyQueueData(restaurantId, userCount);
	}

	private void verifyQueueData(UUID restaurantId, int expectedUserCount) {
		String redisKey = "queue:restaurant:" + restaurantId + ":usersList";

		// Redis에서 등록된 사용자 수 확인
		Long queueSize = redisTemplate.opsForZSet().size(redisKey);
		assertThat(queueSize).isEqualTo(expectedUserCount);

		// Redis에서 모든 사용자와 score를 가져와 고유한지 확인
		Set<ZSetOperations.TypedTuple<Object>> queueEntries =
			redisTemplate.opsForZSet().rangeWithScores(redisKey, 0, -1);

		assertThat(queueEntries).isNotNull();
		assertThat(queueEntries).hasSize(expectedUserCount);

		// 모든 score가 고유한지 확인
		Set<Double> uniqueScores = queueEntries.stream()
			.map(ZSetOperations.TypedTuple::getScore)
			.collect(Collectors.toSet());
		assertThat(uniqueScores).hasSize(expectedUserCount);

		logger.info("All users successfully registered with unique scores.");
	}


@Test
	public void testLockingInRegisterQueue() throws InterruptedException {
		UUID restaurantId = UUID.randomUUID();

		Thread thread1 = new Thread(() -> {
			logger.info("스레드 1: 대기열 등록 시도...");
			queueService.registerQueue(new QueueRegisterDto(
				restaurantId, 1L, 4, QueueType.ONLINE, DiningOption.IN_STORE
			));
			logger.info("스레드 1: 대기열 등록 완료.");
		});

		Thread thread2 = new Thread(() -> {
			try {
				Thread.sleep(100); // 스레드 간의 충돌 유발
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			logger.info("스레드 2: 대기열 등록 시도...");
			queueService.registerQueue(new QueueRegisterDto(
				restaurantId, 2L, 4, QueueType.ONLINE, DiningOption.IN_STORE
			));
			logger.info("스레드 2: 대기열 등록 완료.");
		});

		thread1.start();
		thread2.start();

		thread1.join();
		thread2.join();

		String waitingListKey = RedisKeyUtil.getWaitingListKey(restaurantId);
		Long size = redisTemplate.opsForZSet().size(waitingListKey);
		assertThat(size).isEqualTo(2);

		String user1QueueDataKey = RedisKeyUtil.getUserQueueDataKey(restaurantId, Long.valueOf("1"));
		Object user1Position = redisTemplate.opsForHash().get(user1QueueDataKey, "position");
		String user2QueueDataKey = RedisKeyUtil.getUserQueueDataKey(restaurantId, Long.valueOf("2"));
		Object user2Position = redisTemplate.opsForHash().get(user2QueueDataKey, "position");

		assertThat(user1Position).isNotNull();
		assertThat(user2Position).isNotNull();
		assertThat(user1Position).isNotEqualTo(user2Position); // 서로 다른지 확인
	}

}
