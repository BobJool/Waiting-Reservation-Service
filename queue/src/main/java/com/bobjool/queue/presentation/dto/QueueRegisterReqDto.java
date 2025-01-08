package com.bobjool.queue.presentation.dto;

import java.util.UUID;

import com.bobjool.queue.application.dto.redis.QueueRegisterDto;
import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record QueueRegisterReqDto(
	@NotNull(message = "식당 ID 는 필수 입력값입니다.")
	UUID restaurantId,
	@NotNull(message = "유저 ID 는 필수 입력값입니다.")
	Long userId,
	@NotNull(message = "웨이팅 인원은 필수 입력값입니다.")
	@Positive(message = "인원은 양수여야 합니다.")
	int member,
	@NotNull(message = "웨이팅 방식은 필수 입력값입니다.")
	QueueType type,
	@NotNull(message = "이용 방식은 필수 입력값입니다.")
	DiningOption diningOption
) {
	public QueueRegisterDto toServiceDto() {
		return new QueueRegisterDto(restaurantId, userId, member, type, diningOption);
	}
}
