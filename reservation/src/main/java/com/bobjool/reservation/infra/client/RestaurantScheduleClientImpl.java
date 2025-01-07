package com.bobjool.reservation.infra.client;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.reservation.application.client.RestaurantScheduleClient;
import com.bobjool.reservation.application.client.RestaurantScheduleResDto;
import com.bobjool.reservation.application.client.RestaurantScheduleReserveReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "restaurant-service")
public interface RestaurantScheduleClientImpl extends RestaurantScheduleClient {


        @PostMapping("/api/v1/restaurants/schedule/reserve/{scheduleId}")
        ApiResponse<RestaurantScheduleResDto> reserveSchedule2(
        @PathVariable("scheduleId") UUID scheduleId,
        @RequestBody RestaurantScheduleReserveReqDto scheduleReserveReqDto
    );
}
