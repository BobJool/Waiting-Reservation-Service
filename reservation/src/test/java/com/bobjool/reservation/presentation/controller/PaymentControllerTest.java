package com.bobjool.reservation.presentation.controller;

import com.bobjool.reservation.application.service.PaymentService;
import com.bobjool.reservation.presentation.dto.PaymentCreateReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 컨트롤러 계층에 존재하는 스프링 빈만 띄운 환경입니다.
 * `@RestController`, @RestControllerAdvice
 * */
@ActiveProfiles("test")
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 가짜 객체
    @MockBean
    private PaymentService paymentService;

    @DisplayName("createPayment - 성공")
    @Test
    void createPayment() throws Exception {
        // given - 정상 요청일 때
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateReqDto paymentCreateReqDto = new PaymentCreateReqDto(reservationId, userId, amount, method, pgName);

        // when & then - 201 상태코드 반환하는지 본다
        mockMvc.perform(post("/api/v1/payments")
                .content(objectMapper.writeValueAsString(paymentCreateReqDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }

    @DisplayName("createPayment - reservationId가 없을 때")
    @Test
    void createPayment_whenNonReservationId() throws Exception {
        // given - reservationId 가 없을 떄
        UUID reservationId = null;
        Long userId = 12345L;
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateReqDto paymentCreateReqDto = new PaymentCreateReqDto(reservationId, userId, amount, method, pgName);

        // when & then - 400 상태코드 반환하는지 본다
        mockMvc.perform(post("/api/v1/payments")
                        .content(objectMapper.writeValueAsString(paymentCreateReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("예약 ID 는 필수 입력값입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("createPayment - userId가 없을 때")
    @Test
    void createPayment_whenNonUserId() throws Exception {
        // given - userId가 null인 요청 생성
        UUID reservationId = UUID.randomUUID();
        Long userId = null;
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateReqDto paymentCreateReqDto = new PaymentCreateReqDto(reservationId, userId, amount, method, pgName);

        // when & then - 400 상태코드 반환 및 메시지 확인
        mockMvc.perform(post("/api/v1/payments")
                        .content(objectMapper.writeValueAsString(paymentCreateReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("유저 ID 는 필수 입력값입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("createPayment - userId가 음수일 때")
    @Test
    void createPayment_whenNegativeUserId() throws Exception {
        // given - userId가 음수인 요청 생성
        UUID reservationId = UUID.randomUUID();
        Long userId = -1L;
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateReqDto paymentCreateReqDto = new PaymentCreateReqDto(reservationId, userId, amount, method, pgName);

        // when & then - 400 상태코드 반환 및 메시지 확인
        mockMvc.perform(post("/api/v1/payments")
                        .content(objectMapper.writeValueAsString(paymentCreateReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("유저 ID 는 양수여야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("createPayment - amount가 없을 때")
    @Test
    void createPayment_whenNonAmount() throws Exception {
        // given - amount가 null인 요청 생성
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = null;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateReqDto paymentCreateReqDto = new PaymentCreateReqDto(reservationId, userId, amount, method, pgName);

        // when & then - 400 상태코드 반환 및 메시지 확인
        mockMvc.perform(post("/api/v1/payments")
                        .content(objectMapper.writeValueAsString(paymentCreateReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("결제 금액은 필수 입력값입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("createPayment - amount가 음수일 때")
    @Test
    void createPayment_whenNegativeAmount() throws Exception {
        // given - amount가 음수인 요청 생성
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = -1000;
        String method = "CARD";
        String pgName = "TOSS";
        PaymentCreateReqDto paymentCreateReqDto = new PaymentCreateReqDto(reservationId, userId, amount, method, pgName);

        // when & then - 400 상태코드 반환 및 메시지 확인
        mockMvc.perform(post("/api/v1/payments")
                        .content(objectMapper.writeValueAsString(paymentCreateReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("결제 금액은 양수여야 합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("createPayment - method가 없을 때")
    @Test
    void createPayment_whenNonMethod() throws Exception {
        // given - method가 null인 요청 생성
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        String method = null;
        String pgName = "TOSS";
        PaymentCreateReqDto paymentCreateReqDto = new PaymentCreateReqDto(reservationId, userId, amount, method, pgName);

        // when & then - 400 상태코드 반환 및 메시지 확인
        mockMvc.perform(post("/api/v1/payments")
                        .content(objectMapper.writeValueAsString(paymentCreateReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("결제 방식은 필수 입력값입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("createPayment - pgName가 없을 때")
    @Test
    void createPayment_whenNonPgName() throws Exception {
        // given - pgName이 null인 요청 생성
        UUID reservationId = UUID.randomUUID();
        Long userId = 12345L;
        Integer amount = 10_000;
        String method = "CARD";
        String pgName = null;
        PaymentCreateReqDto paymentCreateReqDto = new PaymentCreateReqDto(reservationId, userId, amount, method, pgName);

        // when & then - 400 상태코드 반환 및 메시지 확인
        mockMvc.perform(post("/api/v1/payments")
                        .content(objectMapper.writeValueAsString(paymentCreateReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("PG사는 필수 입력값입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}