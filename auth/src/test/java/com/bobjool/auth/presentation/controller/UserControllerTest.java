//package com.bobjool.auth.application.presentation.controller;
//
//import com.bobjool.application.service.UserService;
//import com.bobjool.common.exception.BobJoolException;
//import com.bobjool.common.exception.ErrorCode;
//import com.bobjool.infrastructure.security.JwtAuthenticationFilter;
//import com.bobjool.presentation.controller.UserController;
//import com.bobjool.presentation.dto.response.UserResDto;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * @WebMvcTest는 컨트롤러만 로드하며, 관련된 의존성은 Mock 처리 또는 Import를 통해 해결합니다.
// * JwtAuthenticationFilter 및 CustomUserDetailsServiceImpl과 같은 의존성 문제를 해결하기 위해 적절히 Mock 처리합니다.
// */
//@WebMvcTest(UserController.class)
//@Import(SecurityConfigTest.class) // SecurityConfigTest를 활용한 Security 비활성화
//class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    // UserService를 Mock 처리
//    @MockBean
//    private UserService userService;
//
//    // JwtUtil을 Mock 처리
//    @MockBean
//    private com.bobjool.application.interfaces.JwtUtil jwtUtil;
//
//    // JwtBlacklistService를 Mock 처리
//    @MockBean
//    private com.bobjool.application.service.JwtBlacklistService jwtBlacklistService;
//
//    // JwtAuthenticationFilter를 Mock 처리
//    @MockBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    // CustomUserDetailsServiceImpl을 Mock 처리
//    @MockBean
//    private com.bobjool.infrastructure.security.CustomUserDetailsServiceImpl customUserDetailsService;
//
//    @Test
//    @DisplayName("getUserById - 성공적으로 유저 정보를 반환한다")
//    void getUserById_Success() throws Exception {
//        // Given
//        Long userId = 1L;
//        UserResDto userResDto = new UserResDto(
//                userId,
//                "testUser",
//                "Test Name",
//                "Test Nickname",
//                "test@example.com",
//                "slackId",
//                "010-1234-5678",
//                true,
//                null
//        );
//
//        // Mock 설정: 서비스 계층의 동작 정의
//        when(userService.getUserById(userId)).thenReturn(userResDto);
//
//        // When & Then: HTTP GET 요청을 수행하고 올바른 결과를 검증
//        mockMvc.perform(get("/api/v1/users/{id}", userId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.id").value(userId))
//                .andExpect(jsonPath("$.data.username").value("testUser"))
//                .andExpect(jsonPath("$.data.name").value("Test Name"))
//                .andExpect(jsonPath("$.data.nickname").value("Test Nickname"))
//                .andExpect(jsonPath("$.data.email").value("test@example.com"))
//                .andExpect(jsonPath("$.data.slackId").value("slackId"))
//                .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
//                .andExpect(jsonPath("$.data.isApproved").value(true));
//    }
//
//    @Test
//    @DisplayName("getUserById - 사용자를 찾을 수 없을 경우 예외 처리")
//    void getUserById_UserNotFound() throws Exception {
//        // Given
//        Long userId = 100L;
//
//        // Mock 설정: 서비스 계층의 동작 정의 (예외 발생)
//        when(userService.getUserById(userId))
//                .thenThrow(new BobJoolException(ErrorCode.USER_NOT_FOUND));
//
//        // When & Then: HTTP GET 요청을 수행하고 올바른 에러 결과를 검증
//        mockMvc.perform(get("/api/v1/users/{id}", userId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.errorCode").value(ErrorCode.USER_NOT_FOUND.name()))
//                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
//    }
//}