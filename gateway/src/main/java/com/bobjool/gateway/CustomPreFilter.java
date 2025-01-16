package com.bobjool.gateway;

import com.bobjool.gateway.exception.*;
import com.bobjool.gateway.infrastructure.jwt.TokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomPreFilter implements GlobalFilter, Ordered {

    private final TokenParser tokenParser;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        log.info("Pre 필터: 요청 URI는 " + path + "입니다.");

        // 경로가 EXCLUDED_PATHS에 포함되면 필터를 통과
        if (isExcludedPath(path)) {
            log.info("Pre 필터: 경로 {} 에 대해 필터를 건너뛰었습니다.", path);
            return chain.filter(exchange);
        }

        try {
            // JWT 추출 및 검증
            String token = tokenParser.getJwt(request);
            String userId = tokenParser.getUserId(token);
            String userRole = tokenParser.getUserRole(token);

            log.info("Pre 필터: 사용자 ID = {}, 역할 = {}", userId, userRole);

            // 요청 헤더에 사용자 정보를 추가
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Role", userRole)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (TokenException e) {
            log.error("Pre 필터: JWT 검증 실패 - {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    // 경로 비교
    private boolean isExcludedPath(String path) {
        return Arrays.stream(ExcludedPaths.values())
                .anyMatch(excluded -> path.matches(excluded.getPath().replace("**", ".*")));
    }
}
