package com.bobjool.reservation.infra.client;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.reservation.application.client.restaurantschedule.RestaurantScheduleClient;
import com.bobjool.reservation.application.client.restaurantschedule.RestaurantScheduleResDto;
import com.bobjool.reservation.application.client.restaurantschedule.RestaurantScheduleReserveReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "restaurant-service", contextId = "restaurantScheduleClient")
public interface RestaurantScheduleClientImpl extends RestaurantScheduleClient {


        @PostMapping("/api/v1/restaurants/schedule/reserve/{scheduleId}")
        ApiResponse<RestaurantScheduleResDto> reserveSchedule2(
        @PathVariable("scheduleId") UUID scheduleId,
        @RequestBody RestaurantScheduleReserveReqDto scheduleReserveReqDto,
        @RequestHeader("X-User-Id") String userId,
        @RequestHeader("X-Role") String role
    );
}
