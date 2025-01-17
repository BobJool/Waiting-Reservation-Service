//package com.bobjool.auth.application.service;
//
//import com.bobjool.application.service.JwtBlacklistService;
//import com.bobjool.application.service.RedisService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.Duration;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class JwtBlacklistServiceTest {
//
//    @Mock
//    private RedisService redisService;
//
//    @InjectMocks
//    private JwtBlacklistService jwtBlacklistService;
//
//    @DisplayName("토큰을 블랙리스트에 추가하는 기능 검증")
//    @Test
//    void testAddToBlacklist() {
//
//        // Given
//        String token = "testToken"; // 테스트용 토큰
//        long expiration = 60000L;   // 만료 시간 (60초)
//
//        // When
//        jwtBlacklistService.addToBlacklist(token, expiration, true); // 블랙리스트에 추가
//
//        // Then: RedisService의 set 메서드가 정확한 인자로 호출되었는지 검증
//        verify(redisService, times(1))
//                .set(eq("blacklist:accessToken:" + token), eq("blacklisted"), any(Duration.class));
//    }
//
//    @DisplayName("토큰이 블랙리스트에 있는지 확인하는 기능 검증")
//    @Test
//    void testIsBlacklisted() {
//
//        // Given
//        String token = "testToken"; // 테스트용 토큰
//        when(redisService.hasKey("blacklist:token:refresh:" + token)).thenReturn(true); // Redis에서 키가 존재한다고 설정
//
//        // When: 블랙리스트 확인
//        boolean isBlacklisted = jwtBlacklistService.isBlacklisted(token, true);
//
//        // Then: 반환 값이 true인지 확인하고, RedisService의 hasKey 메서드 호출 검증
//        assertTrue(isBlacklisted);
//        verify(redisService, times(1)).hasKey("blacklist:token:refresh:" + token);
//    }
//}
