package com.bobjool.common.infra.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestLoggingAspect {

    private final HttpServletRequest request;

    @Before("execution(* com.bobjool..*Controller.*(..))") // 모든 컨트롤러의 메서드 실행 전에 동작
    public void logRequestDetails() {
        // 요청 URL
        String requestURL = request.getRequestURL().toString();

        // 헤더 값 추출
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-Role");

        // 로그 출력
        log.info("API Request - URL: {}, X-User-Id: {}, X-Role: {}", requestURL, userId, role);
    }
}
