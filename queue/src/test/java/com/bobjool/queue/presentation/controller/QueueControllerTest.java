package com.bobjool.queue.presentation.controller;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.bobjool.queue.application.dto.QueueRegisterDto;
import com.bobjool.queue.application.service.QueueService;
import com.bobjool.queue.domain.entity.Queue;
import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueType;
import com.bobjool.queue.domain.repository.QueueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueueControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(QueueControllerTest.class);
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private QueueRepository queueRepository;

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private QueueService queueService;

	@BeforeEach
	void setUp() {
		queueRepository.deleteAll(); // 테스트 실행 전 데이터 정리
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
					//
					// mockMvc.perform(post("/api/v1/queues")
					// 		.content(objectMapper.writeValueAsString(request))
					// 		.contentType(MediaType.APPLICATION_JSON))
					// 	.andExpect(status().isCreated());
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

		// then
		List<Queue> queues = queueRepository.findByRestaurantId(restaurantId);
		assertThat(queues).hasSize(userCount); // 100명이 등록되었는지 확인
		assertThat(queues.stream().map(Queue::getPosition).distinct().count()).isEqualTo(
			userCount); // position이 고유한지 확인
	}

	@Test
	public void testPessimisticLockingInRegisterQueue() throws InterruptedException {
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

		List<Queue> queues = queueRepository.findByRestaurantId(restaurantId);
		assertThat(queues).hasSize(2); // 두 사용자가 성공적으로 등록되었는지 확인
		assertThat(queues.stream().map(Queue::getPosition).distinct().count()).isEqualTo(2); // Position 중복 없음

		queues.forEach(queue -> logger.info("대기열 데이터: userId={}, position={}",
			queue.getUserId(), queue.getPosition()));
	}

}
