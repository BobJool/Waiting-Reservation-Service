package com.bobjool.queue.application.dto;

import java.util.UUID;

import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueType;

public record QueueRegisterDto(
	UUID restaurantId,
	Long userId,
	int member,
	QueueType type,
	DiningOption diningOption
) {

}
