package com.bobjool.queue.presentation.controller;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueueControllerTest {

	// @Autowired
	// private MockMvc mockMvc;
	//
	// @Autowired
	// RedisTemplate<String, Object> redisTemplate;
	//
	//
	// private static final Logger logger = LoggerFactory.getLogger(QueueControllerTest.class);
	//
	//
	// @Autowired
	// private ObjectMapper objectMapper;
	// @Autowired
	// private QueueService queueService;
	//
	// private final UUID restaurantId = UUID.randomUUID();
	//
	//
	// @DisplayName("registerQueue - 동시에 100명이 대기열에 등록")
	// @Test
	// void registerQueue() throws InterruptedException {
	// 	// given
	// 	UUID restaurantId = UUID.randomUUID();
	// 	int userCount = 100;
	//
	// 	ExecutorService executorService = Executors.newFixedThreadPool(userCount);
	// 	ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)executorService;
	// 	CountDownLatch countDownLatch = new CountDownLatch(userCount);
	//
	// 	// when
	// 	for (int i = 1; i <= userCount; i++) {
	// 		final Long userId = (long) i;
	// 		executorService.submit(() -> {
	// 			try {
	// 				String requestBody = String.format(
	// 					"{\"restaurantId\":\"%s\",\"userId\":%d,\"member\":4,\"type\":\"ONLINE\",\"diningOption\":\"IN_STORE\"}",
	// 					restaurantId, userId
	// 				);
	//
	// 				mockMvc.perform(
	// 					post("/api/v1/queues") // 엔드포인트 수정
	// 						.contentType(MediaType.APPLICATION_JSON)
	// 						.content(requestBody)
	// 				).andExpect(status().isAccepted());
	// 			} catch (Exception e) {
	// 				e.printStackTrace();
	// 			} finally {
	// 				countDownLatch.countDown();
	// 			}
	// 		});
	// 		logger.info("excutorService active" + ((ThreadPoolExecutor)executorService).getActiveCount());
	// 	}
	//
	// 	countDownLatch.await();
	// 	executorService.shutdown();
	//
	// 	Thread.sleep(1000); // 데이터 저장 대기
	//
	// 	verifyQueueData(restaurantId, userCount);
	// }
	//
	// private void verifyQueueData(UUID restaurantId, int expectedUserCount) {
	// 	String redisKey = "queue:" + restaurantId + ":usersList";
	//
	// 	// Redis에서 등록된 사용자 수 확인
	// 	Long queueSize = redisTemplate.opsForZSet().size(redisKey);
	// 	assertThat(queueSize).isEqualTo(expectedUserCount);
	//
	// 	// Redis에서 모든 사용자와 score를 가져와 고유한지 확인
	// 	Set<ZSetOperations.TypedTuple<Object>> queueEntries =
	// 		redisTemplate.opsForZSet().rangeWithScores(redisKey, 0, -1);
	//
	// 	assertThat(queueEntries).isNotNull();
	// 	assertThat(queueEntries).hasSize(expectedUserCount);
	//
	// 	// 모든 score가 고유한지 확인
	// 	Set<Double> uniqueScores = queueEntries.stream()
	// 		.map(ZSetOperations.TypedTuple::getScore)
	// 		.collect(Collectors.toSet());
	// 	assertThat(uniqueScores).hasSize(expectedUserCount);
	//
	// 	logger.info("All users successfully registered with unique scores.");
	// }
}
