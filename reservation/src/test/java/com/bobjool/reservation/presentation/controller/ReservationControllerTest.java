package com.bobjool.reservation.presentation.controller;

import com.bobjool.reservation.application.service.ReservationService;
import com.bobjool.reservation.presentation.dto.reservation.ReservationCreateReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @DisplayName("createReservation - 성공")
    @Test
    void createReservation_success() throws Exception {
        // given
        UUID restaurantId = UUID.randomUUID();
        UUID restaurantScheduleId = UUID.randomUUID();
        Long userId = 12345L;
        Integer guestCount = 4;

        ReservationCreateReqDto requestDto = new ReservationCreateReqDto(userId, restaurantId, restaurantScheduleId, guestCount);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("createReservation - userId가 없을 때")
    @Test
    void createReservation_whenUserIdIsNull() throws Exception {
        // given
        UUID restaurantId = UUID.randomUUID();
        UUID restaurantScheduleId = UUID.randomUUID();
        Long userId = null; // userId가 null
        Integer guestCount = 4;

        ReservationCreateReqDto requestDto = new ReservationCreateReqDto(userId, restaurantId, restaurantScheduleId, guestCount);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("유저 ID 는 필수 입력값입니다."));
    }

    @DisplayName("createReservation - 레스토랑 ID가 없을 때")
    @Test
    void createReservation_whenRestaurantIdIsNull() throws Exception {
        // given
        UUID restaurantId = null; // restaurantId가 null
        UUID restaurantScheduleId = UUID.randomUUID();
        Long userId = 12345L;
        Integer guestCount = 4;

        ReservationCreateReqDto requestDto = new ReservationCreateReqDto(userId, restaurantId, restaurantScheduleId, guestCount);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("레스토랑 ID 는 필수 입력값입니다."));
    }

    @DisplayName("createReservation - 레스토랑 스케줄 ID가 없을 때")
    @Test
    void createReservation_whenRestaurantScheduleIdIsNull() throws Exception {
        // given
        UUID restaurantId = UUID.randomUUID();
        UUID restaurantScheduleId = null; // restaurantId가 null
        Long userId = 12345L;
        Integer guestCount = 4;

        ReservationCreateReqDto requestDto = new ReservationCreateReqDto(userId, restaurantId, restaurantScheduleId, guestCount);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("레스토랑 스케줄 ID 는 필수 입력값입니다."));
    }

    @DisplayName("createReservation - 예약 인원수가 0 또는 음수일 때")
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void createReservation_whenGuestCountIsNegative(Integer guestCount) throws Exception {
        // given - 예약 인원수가 0 또는 음수일 때
        UUID restaurantId = UUID.randomUUID();
        UUID restaurantScheduleId = UUID.randomUUID();
        Long userId = 12345L;

        ReservationCreateReqDto requestDto = new ReservationCreateReqDto(userId, restaurantId, restaurantScheduleId, guestCount);

        // when & then
        mockMvc.perform(post("/api/v1/reservations")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("예약 인원수는 양수여야 합니다."));
    }
}
