package com.bobjool.reservation.infra.client;

import com.bobjool.common.presentation.ApiResponse;
import com.bobjool.reservation.application.client.RestaurantScheduleClient;
import com.bobjool.reservation.application.client.RestaurantScheduleResDto;
import com.bobjool.reservation.application.client.RestaurantScheduleReserveReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "restaurant-service", url = "${RESTAURANT_SERVICE_SERVICE_HOST:http://localhost:19020}")
public interface RestaurantScheduleClientImpl extends RestaurantScheduleClient {

    @PatchMapping("/api/v1/restaurants/schedule/{scheduleId}")
    ApiResponse<RestaurantScheduleResDto> reserveSchedule(
            @PathVariable("scheduleId") UUID scheduleId,
            @RequestBody RestaurantScheduleReserveReqDto scheduleReserveReqDto
    );

}
