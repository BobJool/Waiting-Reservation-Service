package com.bobjool.payment;

//import redis.embedded.RedisServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

@Profile("test")
@Configuration
public class EmbeddedRedisConfig {

//    private RedisServer redisServer;
//
//    @PostConstruct
//    public void startRedis() throws IOException {
//        int port = findAvailablePort();
//        redisServer = new RedisServer(port); // 포트 설정
//        redisServer.start();
//    }
//
//    @PreDestroy
//    public void stopRedis() throws IOException {
//        if (redisServer != null) {
//            redisServer.stop();
//        }
//    }
//
//    private int findAvailablePort() {
//        try (ServerSocket socket = new ServerSocket(0)) {
//            return socket.getLocalPort();
//        } catch (IOException e) {
//            throw new RuntimeException("No available port found", e);
//        }
//    }
}
