package com.bobjool.gateway;

import com.bobjool.gateway.exception.*;
import com.bobjool.gateway.infrastructure.jwt.TokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomPostFilter implements GlobalFilter, Ordered {

    private final TokenParser tokenParser;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().toString();

        // 경로가 EXCLUDED_PATHS에 포함되면 필터를 통과
        if (isExcludedPath(path)) {
            log.info("Post 필터: 경로 {} 에 대해 필터를 건너뛰었습니다.", path);
            return chain.filter(exchange);
        }

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            log.info("Post 필터: 응답 상태 코드는 " + response.getStatusCode() + "입니다.");

            try {
                // 토큰에서 User ID와 Role 추출
                String token = tokenParser.getJwt(exchange.getRequest());
                String userId = tokenParser.getUserId(token);
                String userRole = tokenParser.getUserRole(token);

                // 응답 헤더에 사용자 정보 추가
                response.getHeaders().add("X-User-Id", userId);
                response.getHeaders().add("X-Role", userRole);

                log.info("Post 필터: 응답 헤더에 사용자 정보를 추가했습니다. X-User-Id = {}, X-Role = {}", userId, userRole);

            } catch (TokenException e) {
                log.warn("Post 필터: 토큰을 파싱할 수 없습니다 - {}", e.getMessage());
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    // EXCLUDED_PATHS와 요청 경로를 비교하는 메서드
    private boolean isExcludedPath(String path) {
        return Arrays.stream(ExcludedPaths.values())
                .anyMatch(excluded -> path.matches(excluded.getPath().replace("**", ".*")));
    }
}
