package com.bobjool.restaurant.presentation.dto.restaurant;

import com.bobjool.restaurant.application.dto.restaurant.RestaurantCheckOwnerDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record RestaurantCheckOwnerReqDto(
        @NotNull(message = "유저 ID 는 필수 입력값입니다.")
        @Positive(message = "유저 ID 는 양수여야 합니다.")
        Long userId,

        @NotNull(message = "레스트랑 ID는 필수값 입니다.")
        UUID restaurantId
) {

    public RestaurantCheckOwnerDto toServiceDto() {
        return new RestaurantCheckOwnerDto(
                userId,
                restaurantId
        );
    }

}
