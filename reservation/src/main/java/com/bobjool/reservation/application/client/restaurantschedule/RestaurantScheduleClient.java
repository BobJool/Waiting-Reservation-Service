package com.bobjool.reservation.application.client.restaurantschedule;

import com.bobjool.common.presentation.ApiResponse;

import java.util.UUID;

public interface RestaurantScheduleClient {
    ApiResponse<RestaurantScheduleResDto> reserveSchedule2(
            UUID scheduleId,
            RestaurantScheduleReserveReqDto scheduleReserveReqDto,
            String userId,
            String role
    );
}
