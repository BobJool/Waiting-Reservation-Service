package com.bobjool.queue.application.dto;

import java.util.UUID;

import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueType;

public record QueueRegisterDto(
	UUID restaurantId,
	Long userId,
	Integer member,
	QueueType type,
	DiningOption diningOption
) {

	@Override
	public String toString() {
		return "{" +
			"\"restaurantId\":\"" + restaurantId + "\"," +
			"\"userId\":\"" + userId + "\"," +
			"\"member\":\"" + member + "\"," +
			"\"type\":\"" + type + "\"," +
			"\"diningOption\":\"" + diningOption + "\"}";
	}
}
