package com.bobjool.auth.application.service;

import com.bobjool.application.interfaces.JwtUtil;
import com.bobjool.application.service.AuthService;
import com.bobjool.application.service.JwtBlacklistService;
import com.bobjool.common.exception.BobJoolException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtBlacklistService jwtBlacklistService;

    @InjectMocks
    private AuthService authService;

    @DisplayName("유효한 토큰으로 로그아웃 성공 시나리오.")
    @Test
    void testSignOut_Success() {

        // Given
        // 테스트 입력 데이터 및 Mock 설정
        String token = "validToken";
        HttpServletRequest request = mock(HttpServletRequest.class);
        System.out.println(request);

        when(jwtUtil.getTokenFromHeader("Authorization", request)).thenReturn(token); // 헤더에서 토큰 추출
        when(jwtUtil.validateToken(token)).thenReturn(true); // 토큰 유효성 확인
        when(jwtUtil.getTokenType(token)).thenReturn("access"); // 토큰 유형 확인
        when(jwtUtil.getRemainingExpiration(token)).thenReturn(60000L); // 남은 만료 시간 반환

        // When
        authService.signOut(request); // 로그아웃 메서드 호출

        // Then
        verify(jwtBlacklistService, times(1))
                .addToBlacklist(eq(token), eq(60000L), eq(true)); // 블랙리스트 서비스가 호출되었는지 검증
    }

    @DisplayName("유효하지 않은 토큰으로 로그아웃 실패 시나리오")
    @Test
    void testSignOut_InvalidToken() {
        // Given: 테스트 입력 데이터 및 Mock 설정
        HttpServletRequest request = mock(HttpServletRequest.class); // HttpServletRequest Mock 생성
        when(jwtUtil.getTokenFromHeader("Authorization", request)).thenReturn("invalidToken"); // 헤더에서 토큰 추출
        when(jwtUtil.validateToken("invalidToken")).thenReturn(false); // 토큰 유효성 검사 실패

        // When / Then: 로그아웃 호출 시 예외 발생 검증
        assertThrows(BobJoolException.class, () -> authService.signOut(request));
    }
}
