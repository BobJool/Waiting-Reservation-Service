package com.bobjool.queue.application.dto;

import java.util.UUID;

import com.bobjool.queue.domain.enums.DiningOption;
import com.bobjool.queue.domain.enums.QueueType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record QueueRegisterDto(
	@JsonProperty("restaurantId")
	UUID restaurantId,
	@JsonProperty("userId")
	Long userId,
	@JsonProperty("member")
	Integer member,
	@JsonProperty("type")
	QueueType type,
	@JsonProperty("diningOption")
	DiningOption diningOption
) { }
