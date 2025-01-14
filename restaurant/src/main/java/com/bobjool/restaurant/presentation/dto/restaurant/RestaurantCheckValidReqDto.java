package com.bobjool.restaurant.presentation.dto.restaurant;

import com.bobjool.restaurant.application.dto.restaurant.RestaurantCheckOwnerDto;
import com.bobjool.restaurant.application.dto.restaurant.RestaurantCheckValidDto;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RestaurantCheckValidReqDto(
        @NotNull(message = "레스트랑 ID는 필수값 입니다.")
        UUID restaurantId
) {
    public RestaurantCheckValidDto toServiceDto() {
        return new RestaurantCheckValidDto(
                restaurantId
        );
    }

}

